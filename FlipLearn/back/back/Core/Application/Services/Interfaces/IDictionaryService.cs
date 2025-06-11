using back.Core.Domain.DTO;
using back.Core.Domain.Models;

namespace back.Core.Application.Services.Interfaces
{
    public interface IDictionaryService
    {
        Task<List<DictionaryDto>> GetUsersDictionariesAsync(int userId);
        Task<List<DictionaryDto>> GetUsersAvailableDictionariesAsync(int userId);
        Task<bool> IsUserCreator(int userId, int dictionaryId);
        Task<DictionaryExtended?> GetFullDictionaryAsync(int dictionaryId);
        Task<bool> UpdateDictionaryWithCardsAsync(int dictionaryId,
            string newTitle, string newDescription,
            bool IsPublic,
            (string from, string to) langsChange,
            string newLabel,
            string? CEFR,
            List<CardFromClient>? cardsToInsert,
            List<CardToClient>? cardsToUpdate,
            List<int>? cardsToDelete,
            AIСorrectnessService сorrectnessChecker);
        Task<bool> InsertDictionaryWithCardsAsync(DictionaryFromClient dictionary,
            int creatorId, AIСorrectnessService сorrectnessChecker);
        Task<bool> DeleteDictionaryWithCardsAsync(int dictionaryId);
        Task<bool> ChangeDictionaryVisibility(int dictionaryId, AIСorrectnessService сorrectnessChecker);
    }

}
