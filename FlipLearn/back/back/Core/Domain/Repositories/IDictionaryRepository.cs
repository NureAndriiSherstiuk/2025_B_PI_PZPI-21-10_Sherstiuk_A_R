using back.Core.Domain.Models;
using back.Core.Domain.DTO;

namespace back.Core.Domain.Repositories
{
    public interface IDictionaryRepository
    {
        Task<List<DictionaryDto>> GetDictionariesAsync(int take, int lastId, string? titlePattern,
             IEnumerable<string>? labelsPool,  IEnumerable<string>? langFromPool,
             IEnumerable<string>? langToPool, DateTime? dateCreatedFrom, DateTime? dateCreatedTo);
        Task<List<DictionaryDto>> GetUsersDictionariesAsync(int userId);
        Task<List<DictionaryDto>> GetUsersAvailableDictionariesAsync(int userId);
        Task<bool> IsUserCreator(int userId, int dictionaryId);
        Task<DictionaryExtended?> GetFullDictionaryAsync(int dictionaryId);
        Task<bool> UpdateDictionaryWithCardsAsync(int dictionaryId,
            string newTitle, string newDescription,
            (string from, string to) langsChange,
            string newLabel,
            string? CEFR,
            List<CardToClient> cardsToInsert,
            List<CardToClient> cardsToUpdate,
            List<int> cardsToDelete);

        Task<bool> InsertDictionaryWithCardsAsync(
            DictionaryFromClient dictionary, List<CardToClient> checkedCards, int creatorId);

        Task<bool> DeleteDictionaryWithCardsAsync(int dictionaryId);
        Task<List<Dictionary>> GetFullDictionariesAsync(List<int> dictionariesId);
        Task<Dictionary?> GetDictionaryAsync(int dictionaryId);
        Task MakeDictionaryPrivateAsync(int dictionaryId);
        Task MakeDictionaryPublicAsync(int dictionaryId, List<CardToClient> checkedCards);
    }
}
