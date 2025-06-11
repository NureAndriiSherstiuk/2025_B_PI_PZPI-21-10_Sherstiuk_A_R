using AutoMapper;
using back.Core.Domain.Models;
using back.Infrastructure.Persistance.DbConnections;
using Dapper;
using System.Text;
using back.Core.Domain.Repositories;
using back.Core.Domain.DTO;

namespace back.Infrastructure.Persistance.Repositories
{
    public class DictionaryRepository : IDictionaryRepository
    {
        private readonly MsSQLConnectionWrapper wrapper;
        private readonly IMapper mapper;

        public DictionaryRepository(MsSQLConnectionWrapper _connection, IMapper mapper)
        {
            wrapper = _connection;
            this.mapper = mapper;
        }


        public async Task<List<DictionaryDto>> GetDictionariesAsync(
            int take, int lastId,
            string? titlePattern, 
            IEnumerable<string>? labelsPool, 
            IEnumerable<string>? langFromPool, 
            IEnumerable<string>? langToPool, 
            DateTime? dateCreatedFrom, 
            DateTime? dateCreatedTo)
        {
            titlePattern = titlePattern + "%";
            StringBuilder sb = new("Select TOP(@take) * from Dictionaries WHERE id > @lastId ");

            if(!string.IsNullOrWhiteSpace(titlePattern))
                sb.Append("AND Title LIKE @titlePattern ");
            
            if (labelsPool is not null && labelsPool.Any()) 
                sb.Append("AND Label IN (@labelsPool) ");
            
            if (langFromPool is not null && langFromPool.Any())
                sb.Append("AND FromLang IN (@langFromPool) ");
            
            if (langToPool is not null && langToPool.Any())
                sb.Append("AND ToLang IN (@langToPool) ");

            if (dateCreatedFrom.HasValue && dateCreatedFrom.Value != DateTime.MinValue)
                sb.Append("AND CreationDate >= @dateCreatedFrom ");

            if (dateCreatedTo.HasValue && dateCreatedTo.Value != DateTime.MinValue)
                sb.Append("AND CreationDate <= @dateCreatedTo ");

            List<DictionaryDto> res = (await wrapper.Connection.QueryAsync<DictionaryDto>(sb.ToString(),
                new { take, lastId, titlePattern, labelsPool, langFromPool, langToPool, dateCreatedFrom, dateCreatedTo })).ToList();
            return res;
        }

        public async Task<List<DictionaryDto>> GetUsersDictionariesAsync(int userId)
        {
            List<DictionaryDto> res = (await wrapper.Connection.QueryAsync<DictionaryDto>("SELECT d.*, COUNT(c.Id) AS CardsCount " +
                " FROM Dictionaries d LEFT JOIN Cards c ON c.DictionaryId = d.Id WHERE d.CreatorId = @CreatorId " +
                " GROUP BY d.Id, d.Title, d.CreationDate, d.CreatorId, d.Description, d.IsPublic, d.LastModified, d.FromLang, d.ToLang, d.Label, d.CEFR " +
                " ORDER BY d.CreationDate DESC", 
                new { CreatorId = userId })).ToList();

            return res;
        }


        public async Task<List<DictionaryDto>> GetUsersAvailableDictionariesAsync(int userId)
        {
            IEnumerable<DictionaryDto> res = await wrapper.Connection.QueryAsync<DictionaryDto, UserMinimal, DictionaryDto>(
                "Select d.*, COUNT(c.Id) AS CardsCount, u.Id as UserId, u.Username, u.Email ,u.Image from Dictionaries as d join Users as u on d.CreatorId = u.Id " +
                " LEFT JOIN Cards as c ON c.DictionaryId = d.Id " +
                " where d.Id IN (SELECT DictionaryId FROM AccessRights WHERE UserId = @userId) " +
                " GROUP BY d.Id, d.Title, d.CreationDate, d.CreatorId, d.Description, d.IsPublic, d.LastModified, d.FromLang, d.ToLang, d.Label, d.CEFR, u.Id, u.Username, u.Email ,u.Image " +
                " ORDER BY d.CreationDate DESC",
                (dictionary, user) =>
                {
                    user.Id = dictionary.CreatorId;
                    dictionary.Creator = user;
                    return dictionary;
                },
                new { userId },
                splitOn: "UserId");

            return res.ToList();
        }

        public async Task<bool> IsUserCreator(int userId, int dictionaryId)
        {
            Dictionary? dictionary = (await wrapper.Connection.QueryAsync<Dictionary>("Select * from Dictionaries where Id = @dictionaryId",
                new { dictionaryId })).FirstOrDefault();

            if (dictionary is not null)
                return dictionary.CreatorId == userId;

            return false;
        }

        public async Task<DictionaryExtended?> GetFullDictionaryAsync(int dictionaryId)
        {
            Dictionary<int, DictionaryExtended> dictionaryHashTable = new();

            IEnumerable<DictionaryExtended> res = await wrapper.Connection.QueryAsync<DictionaryExtended, CardToClient, DictionaryExtended>(
                "Select d.*, " +
                "c.Id as CardId, c.Term, c.Meaning, c.Translation, c.Status " +
                "from Dictionaries d join Cards c on d.Id = c.DictionaryId where d.Id = @DictionaryId",
                (dictionary, card) =>
                {
                    if (!dictionaryHashTable.TryGetValue(dictionary.Id, out var existingDictionary))
                    {
                        existingDictionary = dictionary;
                        existingDictionary.Cards = new List<CardToClient>();
                        dictionaryHashTable.Add(existingDictionary.Id, existingDictionary);
                    }

                    existingDictionary.Cards.Add(card);
                    return existingDictionary;
                },
                new { DictionaryId = dictionaryId },
                splitOn: "CardId");

            DictionaryExtended? dictionary = dictionaryHashTable.Values.FirstOrDefault();

            UserMinimal? creator = (await wrapper.Connection.QueryAsync<UserMinimal>("Select Id, Username, Email, TrustLevel, Image from Users where Id = @userId",
                new { userId = dictionary?.CreatorId })).FirstOrDefault();
            
            dictionary.Creator = creator;

            return dictionary;
        }

        private (string, DynamicParameters) FormBulkInsertCardsQuery(int dictionaryId, List<CardToClient> cardsToInsert)
        {
            StringBuilder insertCardSql = new("INSERT INTO Cards (DictionaryId, Term, Meaning, Translation, Status) VALUES ");
            var parameters = new DynamicParameters();
            parameters.Add("@DictionaryId", dictionaryId);

            for (int i = 0; i < cardsToInsert.Count; i++)
            {
                var card = cardsToInsert[i];
                insertCardSql.Append($"(@DictionaryId, @Term{i}, @Meaning{i}, @Translation{i}, @Status{i}),");

                parameters.Add($"@Term{i}", card.Term);
                parameters.Add($"@Meaning{i}", card.Meaning);
                parameters.Add($"@Translation{i}", card.Translation);
                parameters.Add($"@Status{i}", card.Status);
            }

            insertCardSql.Remove(insertCardSql.Length - 1, 1).Append(";");
            return (insertCardSql.ToString(), parameters);
        }

        private (string, DynamicParameters) FormBulkDeleteCardsQuery(List<int> cardsToDelete)
        {
            StringBuilder deleteCardsSql = new("Delete from Cards where id in ( ");
            var parameters = new DynamicParameters();

            for (int i = 0; i < cardsToDelete.Count; i++)
            {
                deleteCardsSql.Append($"@cardId{i}, ");
                parameters.Add($"@cardId{i}", cardsToDelete[i]);
            }

            deleteCardsSql.Remove(deleteCardsSql.Length - 2, 2).Append(");");
            return (deleteCardsSql.ToString(), parameters);
        }

        public async Task<bool> UpdateDictionaryWithCardsAsync(int dictionaryId,
            string newTitle, string newDescription,
            (string from, string to) langsChange,
            string newLabel,
            string? CEFR,
            List<CardToClient> cardsToInsert,
            List<CardToClient> cardsToUpdate,
            List<int> cardsToDelete)
        {
            using var transaction = wrapper.Connection.BeginTransaction();
            try
            {
                string updateDictionarySql = "Update Dictionaries set " +
                    "Title = @newTitle, Description = @newDescription, LastModified = Getdate(), " +
                    "FromLang = @FromLang, ToLang = @ToLang, Label = @newLabel, CEFR = @newCEFR WHERE id = @dictionaryId";

                await wrapper.Connection.ExecuteAsync(updateDictionarySql,
                    new { newTitle, newDescription, FromLang = langsChange.from, ToLang = langsChange.to, newLabel, newCEFR = CEFR ,dictionaryId }, 
                    transaction);


                if (cardsToDelete.Any())
                {
                    (string deleteCardsSql, DynamicParameters parameters) =
                        FormBulkDeleteCardsQuery(cardsToDelete);
                    await wrapper.Connection.ExecuteAsync(deleteCardsSql, parameters, transaction);
                }

                if (cardsToUpdate.Any())
                {
                    foreach (var card in cardsToUpdate)
                    {
                        await wrapper.Connection.ExecuteAsync(
                            "UPDATE Cards SET Term = @Term, Meaning = @Meaning, " +
                            "Translation = @Translation, Status = @Status WHERE Id = @CardId",
                            new { card.Term, card.Meaning, card.Translation, card.Status, card.CardId },
                            transaction);
                    }
                }

                if (cardsToInsert.Any())
                {
                    (string insertCardSql, DynamicParameters parameters) =
                        FormBulkInsertCardsQuery(dictionaryId, cardsToInsert);
                    await wrapper.Connection.ExecuteAsync(insertCardSql, parameters, transaction);
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

        public async Task<bool> InsertDictionaryWithCardsAsync(DictionaryFromClient dictionary, 
            List<CardToClient> checkedCards, int creatorId)
        {
            using var transaction = wrapper.Connection.BeginTransaction();
            try
            {
                var insertDictionarySql = "INSERT INTO Dictionaries " +
                    "(Title, CreatorId, Description, IsPublic, FromLang, ToLang, Label, CEFR) " +
                    "VALUES (@Title, @CreatorId, @Description, @IsPublic, @FromLang, @ToLang, @Label, @CEFR); " +
                    "SELECT CAST(SCOPE_IDENTITY() as int);";

                int dictionaryId = await wrapper.Connection.ExecuteScalarAsync<int>(insertDictionarySql,
                    new
                    {
                        dictionary.Title,
                        creatorId,
                        dictionary.Description,
                        dictionary.IsPublic,
                        dictionary.FromLang,
                        dictionary.ToLang,
                        dictionary.Label,
                        dictionary.CEFR
                    }, transaction);


                (string insertCardSql, DynamicParameters parameters) = FormBulkInsertCardsQuery(dictionaryId, checkedCards);

                await wrapper.Connection.ExecuteAsync(insertCardSql, parameters, transaction);

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

        public async Task<bool> DeleteDictionaryWithCardsAsync(int dictionaryId)
        {
            int rowsAffected = await wrapper.Connection.ExecuteAsync(
                "Delete from Dictionaries where id = @dictionaryId", new { dictionaryId });
            if (rowsAffected == 1)
                return true;

            return false;
        }

        public async Task<List<Dictionary>> GetFullDictionariesAsync(List<int> dictionariesId)
        {
            Dictionary<int, DictionaryToClient> dictionaryHashTable = new();

            IEnumerable<DictionaryToClient> res = await wrapper.Connection.QueryAsync<DictionaryToClient, CardToClient, DictionaryToClient>(
                "Select d.Id, d.Title, d.CreationDate, d.CreatorId, d.Description, d.IsPublic, d.LastModified, d.FromLang, d.ToLang, d.Label, d.CEFR, " +
                "c.Id as CardId, c.Term, c.Meaning, c.Translation, c.Status " +
                "from Dictionaries d join Cards c on d.Id = c.DictionaryId where d.Id in @DictionaryIds",
                (dictionary, card) =>
                {
                    if (!dictionaryHashTable.TryGetValue(dictionary.Id, out var existingDictionary))
                    {
                        existingDictionary = dictionary;
                        existingDictionary.Cards = new List<CardToClient>();
                        dictionaryHashTable.Add(existingDictionary.Id, existingDictionary);
                    }

                    existingDictionary.Cards.Add(card);
                    return existingDictionary;
                },
                new { DictionaryIds = dictionariesId },
                splitOn: "CardId"
            );

            List<Dictionary> dictionaries = mapper.Map<List<Dictionary>>(dictionaryHashTable.Values.ToList());
            return dictionaries;
        }

        public async Task<Dictionary?> GetDictionaryAsync(int dictionaryId)
        {
            Dictionary? dictionary = await wrapper.Connection.QueryFirstOrDefaultAsync<Dictionary>("Select * from Dictionaries where Id = @dictionaryId",
                new { dictionaryId });

            return dictionary;
        }

        public async Task MakeDictionaryPrivateAsync(int dictionaryId)
        {
            await wrapper.Connection.ExecuteAsync("Update Dictionaries set IsPublic = 0 where Id = @dictionaryId", 
                new { dictionaryId });
        }

        public async Task MakeDictionaryPublicAsync(int dictionaryId, List<CardToClient> checkedCards)
        {
            using var transaction = wrapper.Connection.BeginTransaction();
            try
            {
                await wrapper.Connection.ExecuteAsync("Update Dictionaries set IsPublic = 1 where Id = @dictionaryId",
                    new { dictionaryId }, transaction);

                if (checkedCards.Any())
                {
                    foreach (var card in checkedCards)
                    {
                        await wrapper.Connection.ExecuteAsync(
                            "UPDATE Cards SET Status = @Status WHERE Id = @CardId",
                            new { card.Status, card.CardId }, transaction);
                    }
                }

                transaction.Commit();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                transaction.Rollback();
            }
        }
    }
}
