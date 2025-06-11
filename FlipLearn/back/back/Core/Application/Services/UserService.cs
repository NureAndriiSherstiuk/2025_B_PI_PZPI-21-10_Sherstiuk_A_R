using back.Core.Domain.Repositories;
using back.Core.Domain.DTO;
using back.Core.Domain.Models;
using back.Core.Application.Services.Interfaces;

namespace back.Core.Application.Services
{
    public class UserService : IUserService
    {
        private IUserRepository repository;
        public UserService(IUserRepository _repository)
        {
            repository = _repository;
        }

        public async Task<List<UserMinimal>> GetMinimalUsersAsync(string query, int offset)
        {
            return  await repository.FetchMinimalUsersByUsernameAsync(query, offset);
        }

        public async Task<List<UserMinimal>> GetMinimalUsersAsync(IEnumerable<int> ids)
        {
            return await repository.FetchMinimalUsersByIdsAsync(ids);
        }

        public async Task<User?> GetUserAsync(int id)
        {
            return await repository.GetUserAsync(id);
        }

        public async Task<User?> GetUserByEmailAsync(string email)
        {
            return await repository.GetUserByEmailAsync(email);
        }

        public async Task<UserWithoutPassword?> GetUserWithoutPasswordAsync(int id)
        {
            return await repository.GetUserWithoutPasswordAsync(id);
        }

        public async Task<UserMinimal?> GetUserMinimalAsync(int id)
        {
            return await repository.GetUserMinimalAsync(id);
        }

        public async Task<User?> GetUserByEmailAndPasswordAsync(string email, string password)
        {
            return await repository.GetUserByEmailAndPasswordAsync(email, password);
        }

        public async Task<(bool, string?)> CheckUserExistence(string email, string username)
        {
            User? user = await repository.GetUserByEmailOrUserNameAsync(email, username);
            
            if(user is not null)
            {
                if(user.Username == username)
                    return (true, nameof(username));

                if (user.Email == email)
                    return (true, nameof(email));
            }

            return (false, null);
        }

        public async Task<User?> InsertUserAsync(User user)
        {
            return await repository.InsertUserAsync(user);
        }

        public async Task UpdatePasswordAsync(string email, string password)
        {
            string salt = BCrypt.Net.BCrypt.GenerateSalt(8);

            string hashedPassword = BCrypt.Net.BCrypt.HashPassword(password, salt);
            
            await repository.UpdatePasswordAsync(email, hashedPassword);
        }

        public async Task UpdateUsernameAsync(int id, string username)
        {
            await repository.UpdateUsernameAsync(id, username);
        }

        public async Task UpdateImageAsync(int id, string image)
        {
            await repository.UpdateImageAsync(id, image);
        }
    }
}
