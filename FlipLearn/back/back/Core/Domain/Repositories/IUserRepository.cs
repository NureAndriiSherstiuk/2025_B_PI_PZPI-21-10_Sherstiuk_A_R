using back.Core.Domain.DTO;
using back.Core.Domain.Models;
using System.Threading.Tasks;

namespace back.Core.Domain.Repositories
{
    public interface IUserRepository
    {
        Task<List<UserMinimal>> FetchMinimalUsersByUsernameAsync(string query, int offset);
        Task<List<UserMinimal>> FetchMinimalUsersByIdsAsync(IEnumerable<int> ids);
        Task<User?> GetUserAsync(int id);
        Task<User?> GetUserByEmailAsync(string email);
        Task<UserWithoutPassword?> GetUserWithoutPasswordAsync(int id);
        Task<UserMinimal?> GetUserMinimalAsync(int id);
        Task<User?> GetUserByEmailOrUserNameAsync(string email, string username);
        Task<User?> GetUserByEmailAndPasswordAsync(string email, string password);
        Task<User?> InsertUserAsync(User user);
        Task UpdatePasswordAsync(string email, string password);
        Task UpdateUsernameAsync(int id, string username);
        Task UpdateImageAsync(int id, string image);
    }
}
