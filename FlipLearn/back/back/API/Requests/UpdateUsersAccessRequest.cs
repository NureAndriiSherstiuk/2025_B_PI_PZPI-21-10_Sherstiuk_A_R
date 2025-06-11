using back.Core.Domain.Records;

namespace back.API.Requests
{
    public class UpdateUsersAccessRequest
    {
        public int dictionaryId { get; set; }
        public List<AccessData>? accessToInsert { get; set; }
        public List<AccessData>? accessToUpdate { get; set; }
        public List<AccessData>? accessToDelete { get; set; }
    }
}
