
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace back.Core.Application.Business_Entities
{
    public class Test
    {
        public string FromLanguage { get; set; }
        public string ToLanguage { get; set; }
        [BsonRepresentation(BsonType.DateTime)]
        public DateTime Generated { get; set; }
        public List<DictionaryForRoom> DictionariesInvolved { get; set; }
        public List<Question> Questions { get; set; }
    }
}
