using back.Core.Domain.Models;
using System.Collections.Concurrent;

namespace back.Core.Application.Services
{
    public class RoomsService
    {
        private static ConcurrentDictionary<int, Room> rooms = new();
        private static ConcurrentDictionary<Guid, Room> roomsGuid = new();

        public IEnumerable<Room> GetRooms() => rooms.Values;
        public void AddRoom(int userId, Room room)
        {
            rooms[userId] = room;
            roomsGuid[room.Id] = room;
        }
        public void DeleteRoom(int userId, Guid roomId)
        {
            rooms.Remove(userId, out _);
            roomsGuid.Remove(roomId, out _);
        }
        public Room? GetRoomIfCreator(int userId)
        {
            rooms.TryGetValue(userId, out var room);
            return room;
        }
        public Room? GetRoomById(Guid roomId)
        {
            roomsGuid.TryGetValue(roomId, out var room);
            return room;
        }
    }
}
