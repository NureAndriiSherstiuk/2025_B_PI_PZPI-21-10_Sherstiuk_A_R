using back.Core.Domain.Records;
using back.Infrastructure.Persistance.DbConnections;
using Dapper;
using System.Text;
using back.Core.Domain.Repositories;
using back.Core.Domain.DTO;
using System.Transactions;


namespace back.Infrastructure.Persistance.Repositories
{
    public class AccessRepository : IAccessRepository
    {
        private readonly MsSQLConnectionWrapper wrapper;

        public AccessRepository(MsSQLConnectionWrapper connection)
        {
            wrapper = connection;
        }

        public async Task<List<UserAccess>> GetDictionaryAccessAsync(int dictionaryId)
        {
            var res = (await wrapper.Connection.QueryAsync<UserAccess>("SELECT u.Id, u.Username, u.Email, u.Image, ar.Access " +
                " FROM AccessRights AS ar join users as u on ar.UserId = u.Id where ar.DictionaryId = @dictionaryId",
                new { dictionaryId })).ToList();
            return res;
        }

        public async Task<string?> GetUserPermission(int userId, int dictionaryId)
        {
            string? permission = await wrapper.Connection.QueryFirstOrDefaultAsync<string>("Select Access from AccessRights where " +
                " DictionaryId = @dictionaryId and UserId = @userId",
                new { dictionaryId, userId });

            return permission;
        }

        public async Task<bool> AddUsersAccessAsync(int dictionaryId, List<AccessData> access)
        {
            (string query, DynamicParameters parameters) = FormBulkAddingUsersAccessQuery(dictionaryId, access);
            int rows = await wrapper.Connection.ExecuteAsync(query, parameters);
            return rows > 0;
        }

        private (string, DynamicParameters) FormBulkAddingUsersAccessQuery(int dictionaryId, List<AccessData> access)
        {
            StringBuilder insertAccessSql = new("INSERT INTO AccessRights (DictionaryId, UserId, Access) VALUES ");
            DynamicParameters parameters = new();
            parameters.Add($"@DictionaryId", dictionaryId);

            for (int i = 0; i < access.Count; i++)
            {
                var accessData = access[i];
                insertAccessSql.Append($"(@DictionaryId, @UserId{i}, @Access{i}),");

                parameters.Add($"@UserId{i}", accessData.userId);
                parameters.Add($"@Access{i}", accessData.access);
            }

            insertAccessSql.Remove(insertAccessSql.Length - 1, 1).Append(";");
            return (insertAccessSql.ToString(), parameters);
        }

        private (string, DynamicParameters) FormBulkDeleteUsersAccessQuery(int dictionaryId, List<AccessData> access)
        {
            StringBuilder deleteSql = new("DELETE FROM AccessRights WHERE DictionaryId = @DictionaryId and UserId IN ( ");
            DynamicParameters parameters = new();
            parameters.Add("@DictionaryId", dictionaryId);

            for (int i = 0; i < access.Count; i++)
            {
                deleteSql.Append($"@UserId{i},");
                parameters.Add($"@UserId{i}", access[i].userId);
            }

            deleteSql.Remove(deleteSql.Length - 1, 1).Append(");");
            return (deleteSql.ToString(), parameters);
        }

        private (string, object) FormUpdateUsersAccessQuery(int dictionaryId, AccessData access)
        {
            return
                ("UPDATE AccessRights SET Access = @Access WHERE DictionaryId = @DictionaryId AND UserId = @UserId",
                new { Access = access.access, DictionaryId = dictionaryId, UserId = access.userId });
        }

        public async Task<bool> UpdateAccessByCreatorAsync(int dictionaryId,
            List<AccessData> accessToInsert,
            List<AccessData> accessToUpdate,
            List<AccessData> usersAccessToDelete)
        {
            using var transaction = wrapper.Connection.BeginTransaction();
            try
            {
                if (usersAccessToDelete.Any())
                {
                    (string query, DynamicParameters parameters) = FormBulkDeleteUsersAccessQuery(dictionaryId, usersAccessToDelete);
                    await wrapper.Connection.ExecuteAsync(query, parameters, transaction);
                }

                if (accessToUpdate.Any())
                {
                    foreach (AccessData access in accessToUpdate)
                    {
                        (string query, object parameters) = FormUpdateUsersAccessQuery(dictionaryId, access);
                        await wrapper.Connection.ExecuteAsync(query, parameters, transaction);
                    }
                }

                if (accessToInsert.Any())
                {
                    (string query, DynamicParameters parameters) = FormBulkAddingUsersAccessQuery(dictionaryId, accessToInsert);
                    await wrapper.Connection.ExecuteAsync(query, parameters, transaction);
                }

                transaction.Commit();
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                transaction.Rollback();
                return false;
            }
        }

        public async Task<bool> UpdateAccessByCoAuthorAsync(int dictionaryId,
            List<AccessData>? accessToInsert,
            List<AccessData>? usersAccessToDelete)
        {
            return await UpdateAccessByCreatorAsync(dictionaryId, accessToInsert, null, usersAccessToDelete);
        }
    }
}
