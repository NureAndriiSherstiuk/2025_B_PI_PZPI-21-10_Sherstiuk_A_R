using back.Core.Application.Services;
using back.Core.Domain.Models;
using Microsoft.AspNetCore.Authorization;
using back.Core.Application.Business_Entities;
using back.Core.Domain.DTO;
using AutoMapper;
using back.Core.Domain.Records;
using back.API.Hubs.Interfaces;


namespace back.API.Hubs
{
    [Authorize]
    public class RoomsHub : IdentityHub<IRoomsClient>
    {
        private RoomsService roomsService;
        private IMapper mapper;
        public RoomsHub(RoomsService roomsService, IMapper mapper)
        {
            this.roomsService = roomsService;
            this.mapper = mapper;
        }

        public async Task SendRooms()
        {
            IEnumerable<Room> rooms = roomsService.GetRooms().Where(room => room.State == RoomState.Forming);
            var roomsMin = mapper.Map<IEnumerable<RoomMinimal>>(rooms);
            await Clients.Caller.ShowRooms(roomsMin);
        }

        public async Task AddRoom(Test test, string roomName)
        {
            UserMinimal user = DetermineUser(Context);

            Room room = new()
            {
                Id = Guid.NewGuid(),
                Name = roomName,
                State = RoomState.Forming,
                Creator = user,
                Test = test,
                Participants = [],
                Results = new List<ParticipantRaceResult>(),
                CEFRMin = CEFRLevelHelper.GetMinLevel(test.DictionariesInvolved.Select(d => d.CEFR)),
                CEFRMax = CEFRLevelHelper.GetMaxLevel(test.DictionariesInvolved.Select(d => d.CEFR)),
                TotalTimeInSeconds = test.Questions.Count * 20
            };

            roomsService.AddRoom(user.Id, room);

            await Clients.Caller.RedirectToRoom(room.Id);

            await Clients.All.RoomWasAdded(mapper.Map<RoomMinimal>(room));
        }

    }
}
