using back.Core.Domain.DTO;
using back.Core.Domain.Records;

namespace back.Core.Application.Services.Interfaces
{
    public interface IAccessService
    {
        Task<List<UserAccess>> GetDictionaryAccessAsync(int dictionaryId);
        Task<Access> GetUserPermission(int userId, int dictionaryId);
        Task<bool> AddUsersAccessAsync(int dictionaryId, List<AccessData> access);
        Task<bool> UpdateAccessByCreatorAsync(int dictionaryId,
            List<AccessData>? accessToInsert,
            List<AccessData>? accessToUpdate,
            List<AccessData>? usersAccessToDelete);
        Task<bool> UpdateAccessByCoAuthorAsync(int dictionaryId,
            List<AccessData>? accessToInsert,
            List<AccessData>? usersAccessToDelete);
    }
}
