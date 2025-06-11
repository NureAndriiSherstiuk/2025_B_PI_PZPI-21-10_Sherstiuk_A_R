using back.Core.Domain.Models;

namespace back.API.Hubs.Interfaces
{
    public interface IRoomsClient
    {
        Task ShowRooms(IEnumerable<RoomMinimal> rooms);
        Task RoomWasAdded(RoomMinimal roomMin);
        Task RoomWasDeleted(Guid roomId);
        Task RedirectToRoom(Guid roomId);
        Task UpdateRoomParticipantsCount(Guid roomId, int count);
    }
}
