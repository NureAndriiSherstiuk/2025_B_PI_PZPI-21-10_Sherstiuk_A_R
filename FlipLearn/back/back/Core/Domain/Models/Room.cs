using back.Core.Application.Business_Entities;
using back.Core.Domain.DTO;
using back.Core.Domain.Records;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.Serializers;

namespace back.Core.Domain.Models
{
    public class Room
    {
        public Guid Id { get; set; }
        public required string Name { get; set; }
        public RoomState State { get; set; }
        public required UserMinimal Creator { get; set; }
        public required Test Test { get; set; }
        public DateTime StartTime { get; set; }
        public int TotalTimeInSeconds { get; set; }
        public string? CEFRMin {  get; set; }
        public string? CEFRMax { get; set; }
        public Timer? Timer { get; set; }
        public required List<UserMinimal> Participants { get; set; }
        public required ICollection<ParticipantRaceResult> Results { get; set; }
    }

    public class RoomMinimal
    {
        public Guid Id { get; set; }
        public required string Name { get; set; }
        public UserMinimal Creator { get; set; }
        public string FromLanguage { get; set; }
        public string ToLanguage { get; set; }
        public int QuestionsCount { get; set; }
        public int ParticipantsCount { get; set; }
        public int DictionariesCount { get; set; }
        public int RaceDurationInSec { get; set; }
        public string? CEFRMin {  get; set; }
        public string? CEFRMax { get; set; }
    }
    
    public class MongoRoom
    {
        [BsonId]
        public ObjectId MongoRoomId { get; set; }
        [BsonGuidRepresentation(GuidRepresentation.Standard)]
        public Guid Id { get; set; }
        public string Name { get; set; }
        public int CreatorId { get; set; }
        public Test Test { get; set; }
        [BsonRepresentation(BsonType.DateTime)]
        public DateTime StartTime { get; set; }
        public ICollection<int> ParticipantsIds { get; set; }
        public ICollection<ParticipantRaceResult> Results { get; set; }
    }

    public class ArchivedRoomMin
    {
        [BsonId]
        public string MongoRoomId { get; set; }
        public Guid Id { get; set; }
        public string Name { get; set; }
        public UserMinimal? Creator { get; set; }
        public string FromLanguage { get; set; }
        public string ToLanguage { get; set; }
        public int QuestionsCount { get; set; }
        public string? CEFRMin { get; set; }
        public string? CEFRMax { get; set; }
        [BsonRepresentation(BsonType.DateTime)]
        public DateTime StartTime { get; set; }
        public List<UserMinimal> Participants { get; set; }
    }

    public class ArchivedRoom
    {
        public string MongoRoomId { get; set; }
        public Guid Id { get; set; }
        public string Name { get; set; }
        public UserMinimal? Creator { get; set; }
        public Test Test { get; set; }
        public DateTime StartTime { get; set; }
        public List<UserMinimal> Participants { get; set; }
        public ICollection<ParticipantRaceResult> Results { get; set; }
    }
}
