using back.Core.Domain.DTO;
using back.Core.Domain.Models;

namespace back.Core.Application.Services.Interfaces
{
    public interface IUserService
    {
        Task<List<UserMinimal>> GetMinimalUsersAsync(string query, int offset);
        Task<List<UserMinimal>> GetMinimalUsersAsync(IEnumerable<int> ids);
        Task<User?> GetUserAsync(int id);
        Task<User?> GetUserByEmailAsync(string email);
        Task<UserMinimal?> GetUserMinimalAsync(int id);
        Task<(bool, string?)> CheckUserExistence(string email, string username);
        Task<User?> GetUserByEmailAndPasswordAsync(string email, string password);
        Task<User?> InsertUserAsync(User user);
        Task UpdatePasswordAsync(string email, string password);
    }
}
