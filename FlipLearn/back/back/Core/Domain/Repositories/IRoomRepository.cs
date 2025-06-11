using back.Core.Domain.DTO;
using back.Core.Domain.Models;
using MongoDB.Bson;

namespace back.Core.Domain.Repositories
{
    public interface IRoomRepository
    {
        Task InsertFinishedRoomAsync(MongoRoom room);
        Task<MongoRoom> GetFinishedRoomAsync(ObjectId roomId);
        Task<List<MongoRoom>> GetFinishedRoomsAsync(int userId);
    }
}
