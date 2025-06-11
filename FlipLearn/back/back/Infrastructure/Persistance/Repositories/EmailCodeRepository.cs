using AutoMapper;
using back.Core.Domain.Models;
using back.Core.Domain.Repositories;
using back.Infrastructure.Persistance.DbConnections;
using Dapper;

namespace back.Infrastructure.Persistance.Repositories
{
    public class EmailCodeRepository : IEmailCodeRepository
    {
        private readonly MsSQLConnectionWrapper wrapper;

        public EmailCodeRepository(MsSQLConnectionWrapper _connection)
        {
            wrapper = _connection;
        }

        public async Task InsertCode(string email, string code, DateTime expirationTime)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync("Insert into EmailCodes VALUES (@email, @code, @expirationTime)",
                    new { email, code, expirationTime });
        }

        public async Task AddOrUpdateCode(string email, string code, DateTime expirationTime)
        {
            if (await GetCodeAsync(email) is null)
            {
                await InsertCode(email, code, expirationTime);
            }
            else
            {
                await RemoveCode(email);
                await InsertCode(email, code, expirationTime);
            }
        }

        public async Task RemoveCode(string email)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync("Delete from EmailCodes where Email = @email",
                new { email });
        }

        public async Task<EmailCode?> GetCodeAsync(string email)
        {
            return await wrapper.Connection.QueryFirstOrDefaultAsync<EmailCode>("Select * from EmailCodes where Email = @email",
                new { email });
        }
    }
}
