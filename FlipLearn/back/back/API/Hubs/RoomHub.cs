using back.Core.Application.Services;
using back.Core.Domain.DTO;
using back.Core.Domain.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;
using back.Core.Domain.Records;
using back.API.Hubs.Interfaces;
using back.Core.Application.Business_Entities;

namespace back.API.Hubs
{
    [Authorize]
    public class RoomHub : IdentityHub<IRoomClient>
    {
        private RoomsService roomsService;
        private IHubContext<RoomsHub, IRoomsClient> roomsHubContext;
        private RoomService roomService;

        public RoomHub(RoomsService roomsService, IHubContext<RoomsHub, IRoomsClient> roomsHubContext, RoomService roomService)
        {
            this.roomsService = roomsService;
            this.roomsHubContext = roomsHubContext;
            this.roomService = roomService;
        }

        public async Task StartRace()
        {
            int userId = int.Parse(Context.UserIdentifier!);

            Room? room = roomsService.GetRoomIfCreator(userId);

            if (room is null)
                throw new HubException("Room not found or you are not room creator");

            room.StartTime = DateTime.UtcNow;
            room.TotalTimeInSeconds = room.Test.Questions.Count * 20;

            room.Timer = new Timer(async _ =>
            {
                int remainingTime = room.TotalTimeInSeconds - (int)(DateTime.UtcNow - room.StartTime).TotalSeconds;
                if (remainingTime <= 0)
                {
                    await ForcedCollectionOfAnswers(room.Id);
                }
                else
                {
                    await Clients.Group($"Room_{room.Id}").UpdateRemainingTime(remainingTime);
                }
            }, null, 1000, 1000);

            room.State = RoomState.InRace;
            await roomsHubContext.Clients.All.RoomWasDeleted(room.Id);

            await Clients.Group($"Room_{room.Id}").StartRace(room.StartTime, room.TotalTimeInSeconds);
        }

        public async Task JoinRoom(Guid roomId)
        {
            Room? room = roomsService.GetRoomById(roomId);
            if (room is null)
                throw new HubException("Room not found");

            Context.Items["RoomId"] = roomId;
            string groupName = $"Room_{roomId}";
            await Groups.AddToGroupAsync(Context.ConnectionId, groupName);

            UserMinimal participant = DetermineUser(Context);
            
            bool userAlreadyInRoom = room.Participants.Any(p => p.Id == participant.Id);

            if (!userAlreadyInRoom)
            {
                room.Participants.Add(participant);
                await Clients.Group(groupName).NotifyUserConnected(participant);
                await roomsHubContext.Clients.All.UpdateRoomParticipantsCount(room.Id, room.Participants.Count);
            }

            bool isCreator = roomsService.GetRoomIfCreator(participant.Id) != null;
            await Clients.Caller.ReceiveRoom(room, isCreator);
        }

        private async Task ForcedCollectionOfAnswers(Guid roomId)
        {
            await Clients.Group($"Room_{roomId}").CollectAnswersAndFinishRace();
        }

        private async Task SaveResultsAndDisposeRace(Room room)
        {
            room.State = RoomState.Finished;
            room.Timer?.Dispose();

            // быстрое уведомление о результатах
            await Clients.Group($"Room_{room.Id}").GetRaceResults(room.Results);

            // save результатов в бд
            await roomService.InsertRoomAsync(room);

            // удаление комнаты из памяти
            roomsService.DeleteRoom(room.Creator.Id, room.Id);
        }

        public async Task ExitRoom(Guid roomId)
        {
            UserMinimal participant = DetermineUser(Context);
            string groupName = $"Room_{roomId}";
            await Groups.RemoveFromGroupAsync(Context.ConnectionId, groupName);

            Room? room = roomsService.GetRoomById(roomId);
            if (room is not null)
            {
                room.Participants.RemoveAll(p => p.Id == participant.Id);
                await Clients.Group(groupName).NotifyUserDisconnected(participant);
                await roomsHubContext.Clients.All.UpdateRoomParticipantsCount(room.Id, room.Participants.Count);
            }
        }

        public async Task FinishRace(Guid roomId, IEnumerable<Question> answers, int correctCount, int wrongCount)
        {
            UserMinimal participant = DetermineUser(Context);
            Room? room = roomsService.GetRoomById(roomId);

            if (room is not null)
            {
                TimeSpan time = DateTime.UtcNow - room.StartTime;
                room.Results.Add(new ParticipantRaceResult() { Participant = participant, Time = time, Answers = answers });
                room.Participants.RemoveAll(u => u.Id == participant.Id);

                if (!room.Participants.Any())
                {
                    await SaveResultsAndDisposeRace(room);
                }
                else
                {
                    await Clients.Group($"Room_{roomId}").ParticipantFinished(participant, correctCount, wrongCount, time);
                }
            }
        }

        // сделать возможность если гонка ещё не началась, выйти и удалить комнату если автор так захотел
        public async Task DissolveRoomByCreator()
        {
            int userId = int.Parse(Context.UserIdentifier!);

            Room? room = roomsService.GetRoomIfCreator(userId);
            
            if (room is null)
                throw new HubException("Room not found or you are not room creator");

            string groupName = $"Room_{room.Id}";

            if (room.State == RoomState.Forming)
            {
                await Clients.Group(groupName).RoomWasDissolved();
                await ExitRoom(room.Id);
                await Groups.RemoveFromGroupAsync(Context.ConnectionId, groupName);
                await roomsHubContext.Clients.All.RoomWasDeleted(room.Id);
                roomsService.DeleteRoom(userId, room.Id);
            }
            else
            {
                throw new HubException("You have already started the race and now you are not able to dissolve this room");
            }
        }
    }
}
