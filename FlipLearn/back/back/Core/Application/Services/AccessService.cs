using back.Core.Application.Services.Interfaces;
using back.Core.Domain.DTO;
using back.Core.Domain.Records;
using back.Core.Domain.Repositories;

namespace back.Core.Application.Services
{
    public class AccessService : IAccessService
    {
        private IAccessRepository repository;

        public AccessService(IAccessRepository _repository)
        {
            repository = _repository;
        }

        public async Task<List<UserAccess>> GetDictionaryAccessAsync(int dictionaryId)
        {
            return await repository.GetDictionaryAccessAsync(dictionaryId);
        }

        public async Task<Access> GetUserPermission(int userId, int dictionaryId)
        {
            string? permission = await repository.GetUserPermission(userId, dictionaryId);

            return permission switch
            {
                "Reader" => Access.Reader,
                "CoAuthor" => Access.CoAuthor,
                _ => Access.None
            };
        }

        public async Task<bool> AddUsersAccessAsync(int dictionaryId, List<AccessData> access)
        {
            return await repository.AddUsersAccessAsync(dictionaryId, access);
        }

        public async Task<bool> UpdateAccessByCreatorAsync(int dictionaryId,
            List<AccessData>? accessToInsert,
            List<AccessData>? accessToUpdate,
            List<AccessData>? usersAccessToDelete)
        {
            return await repository.UpdateAccessByCreatorAsync(dictionaryId, accessToInsert, accessToUpdate, usersAccessToDelete);
        }

        public async Task<bool> UpdateAccessByCoAuthorAsync(int dictionaryId,
            List<AccessData>? accessToInsert,
            List<AccessData>? usersAccessToDelete)
        {
            return await repository.UpdateAccessByCoAuthorAsync(dictionaryId, accessToInsert, usersAccessToDelete);
        }
    }
}
