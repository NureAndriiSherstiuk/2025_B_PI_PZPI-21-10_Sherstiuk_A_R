using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using back.Core.Application.Business_Entities;

namespace back.Core.Domain.DTO
{
    public class ParticipantRaceResult
    {
        public UserMinimal Participant { get; set; }
        public IEnumerable<Question> Answers { get; set; }
        [BsonRepresentation(BsonType.String)]
        public TimeSpan Time {  get; set; }
    }
}
