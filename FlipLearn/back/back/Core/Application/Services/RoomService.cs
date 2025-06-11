using AutoMapper;
using back.Core.Domain.Repositories;
using back.Core.Domain.Models;
using MongoDB.Bson;

namespace back.Core.Application.Services
{
    public class RoomService
    {
        private IRoomRepository roomRepository;
        private IMapper mapper;
        private UserService userService;

        public RoomService(IRoomRepository repository, IMapper mapper, UserService userService)
        {
            roomRepository = repository;
            this.mapper = mapper;
            this.userService = userService;
        }

        public async Task InsertRoomAsync(Room room)
        {
            MongoRoom mongoRoom = mapper.Map<MongoRoom>(room);
            await roomRepository.InsertFinishedRoomAsync(mongoRoom);
        }

        public async Task<ArchivedRoom> GetFinishedRoomAsync(ObjectId roomId)
        {
            MongoRoom mongoRoom = await roomRepository.GetFinishedRoomAsync(roomId);
            ArchivedRoom archivedRoom = mapper.Map<ArchivedRoom>(mongoRoom);
            archivedRoom.Creator = await userService.GetUserMinimalAsync(mongoRoom.CreatorId);
            archivedRoom.Participants = await userService.GetMinimalUsersAsync(mongoRoom.ParticipantsIds);
            return archivedRoom;
        }

        public async Task<List<ArchivedRoomMin>> GetFinishedRoomsAsync(int userId)
        {
            List<MongoRoom> mongoRooms = await roomRepository.GetFinishedRoomsAsync(userId);
            List<ArchivedRoomMin> archivedRooms = [];

            foreach(MongoRoom room in mongoRooms)
            {
                ArchivedRoomMin archivedRoomMin = mapper.Map<ArchivedRoomMin>(room);
                archivedRoomMin.Creator = await userService.GetUserMinimalAsync(room.CreatorId);
                archivedRoomMin.Participants = await userService.GetMinimalUsersAsync(room.ParticipantsIds);

                archivedRooms.Add(archivedRoomMin);
            }

            return archivedRooms;
        }
    }
}
