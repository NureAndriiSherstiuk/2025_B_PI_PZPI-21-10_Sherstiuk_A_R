using Microsoft.Data.SqlClient;
using Dapper;
using back.Core.Domain.Models;
using back.Infrastructure.Persistance.DbConnections;
using back.Core.Domain.Repositories;
using back.Core.Domain.DTO;
using MimeKit.Encodings;

namespace back.Infrastructure.Persistance.Repositories
{
    public class UserRepository : IUserRepository
    {
        private readonly MsSQLConnectionWrapper wrapper;

        public UserRepository(MsSQLConnectionWrapper _connection)
        {
            wrapper = _connection;
        }

        public async Task<List<UserMinimal>> FetchMinimalUsersByUsernameAsync(string query, int offset)
        {
            query = query + "%";
            return (await wrapper.Connection.QueryAsync<UserMinimal>("SELECT Id, Username, Email, TrustLevel, Image " +
                "FROM Users WHERE Username LIKE @query OR Email LIKE @query " +
                "ORDER BY Username, Email " +
                "OFFSET @offset ROWS FETCH NEXT 15 ROWS ONLY;",
                new { query, offset })).ToList();
        }

        public async Task<List<UserMinimal>> FetchMinimalUsersByIdsAsync(IEnumerable<int> ids)
        {
            return (await wrapper.Connection
                        .QueryAsync<UserMinimal>("SELECT Id, Username, Email, TrustLevel, Image from Users where Id in @ids", new { ids })).ToList();
        }

        public async Task<User?> GetUserAsync(int id)
        {
            return await wrapper.Connection.QueryFirstOrDefaultAsync<User>("Select * from Users where Id = @Id", new { Id = id });
        }

        public async Task<User?> GetUserByEmailAsync(string email)
        {
            return await wrapper.Connection.QueryFirstOrDefaultAsync<User>("Select * from Users where Email = @email", new { email });
        }

        public async Task<UserWithoutPassword?> GetUserWithoutPasswordAsync(int id)
        {
            return await wrapper.Connection.QueryFirstOrDefaultAsync<UserWithoutPassword>("Select Id, Username, Email, RegDate, TrustLevel, Image, Lang from Users where Id = @Id", new { Id = id });
        }

        public async Task<UserMinimal?> GetUserMinimalAsync(int id)
        {
            return await wrapper.Connection
                .QueryFirstOrDefaultAsync<UserMinimal>("Select Id, Username, Email, TrustLevel, Image from Users where Id = @Id", new { Id = id });
        }

        public async Task<User?> GetUserByEmailAndPasswordAsync(string email, string password)
        {
            User? user = await wrapper.Connection.QueryFirstOrDefaultAsync<User>("Select * from Users where Email = @Email", new { Email = email });

            if (user is not null)
            {
                bool isPasswordCorrect = BCrypt.Net.BCrypt.Verify(password, user.Password);
                if (isPasswordCorrect)
                    return user;
            }
            return null;
        }

        public async Task<User?> InsertUserAsync(User user)
        {
            string salt = BCrypt.Net.BCrypt.GenerateSalt(8);

            string hashedPassword = BCrypt.Net.BCrypt.HashPassword(user.Password, salt);
            user.Password = hashedPassword;

            int rowsAffected = await wrapper.Connection.ExecuteAsync("Insert into users (Username, email, password) VALUES (@username, @email, @password)",
                new { username = user.Username, email = user.Email, password = user.Password });

            return user;
        }

        public async Task<User?> GetUserByEmailOrUserNameAsync(string email, string username)
        {
            User? user =  await wrapper.Connection.QueryFirstOrDefaultAsync<User>("Select * from Users where Email = @email or Username = @username",
                new { email, username });

            return user;
        }

        public async Task UpdatePasswordAsync(string email, string password)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync("Update Users SET Password = @password where Email = @email",
                new { password, email });
        }

        public async Task UpdateUsernameAsync(int id, string username)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync("Update Users SET Username = @username where Id = @id",
                new { id, username });
        }

        public async Task UpdateImageAsync(int id, string image)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync("Update Users SET Image = @image where Id = @id",
                new { id, image });
        }
    }
}
