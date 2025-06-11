using back.Core.Domain.Models;

namespace back.Core.Domain.Repositories
{
    public interface IEmailCodeRepository
    {
        Task InsertCode(string email, string code, DateTime expirationTime);
        Task RemoveCode(string email);
        Task AddOrUpdateCode(string email, string code, DateTime expirationTime);
        Task<EmailCode?> GetCodeAsync(string email);
    }
}