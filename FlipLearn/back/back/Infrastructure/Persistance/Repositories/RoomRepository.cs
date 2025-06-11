using back.Core.Domain.Models;
using back.Core.Domain.Repositories;
using back.Infrastructure.Persistance.DbConnections;
using MongoDB.Bson;
using MongoDB.Driver;

namespace back.Infrastructure.Persistance.Repositories
{
    public class RoomRepository : IRoomRepository
    {
        private IMongoCollection<MongoRoom> collection;
        public RoomRepository(IMongoClient mongoClient)
        {
            collection = mongoClient.GetDatabase("FlipLearn")
               .GetCollection<MongoRoom>("rooms");
        }

        public async Task<MongoRoom> GetFinishedRoomAsync(ObjectId roomId)
        {
            var filter = Builders<MongoRoom>.Filter.Eq(x => x.MongoRoomId, roomId);
            MongoRoom room = (await collection.FindAsync(filter)).FirstOrDefault();
            return room;
        }

        public async Task<List<MongoRoom>> GetFinishedRoomsAsync(int userId)
        {
            var filter = Builders<MongoRoom>.Filter.AnyEq(x => x.ParticipantsIds, userId);
            List<MongoRoom> rooms = (await collection.FindAsync(filter)).ToList();
            return rooms;
        }

        public async Task InsertFinishedRoomAsync(MongoRoom room)
        {
            await collection.InsertOneAsync(room);
        }
    }
}
