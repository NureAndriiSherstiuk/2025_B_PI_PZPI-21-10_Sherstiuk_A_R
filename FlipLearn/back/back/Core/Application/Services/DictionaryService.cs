using back.Core.Domain.Repositories;
using back.Core.Domain.Models;
using back.Core.Domain.DTO;
using back.Core.Application.Services.Interfaces;
using AutoMapper;
using back.API.Requests;
using System.Security.Cryptography.Xml;


namespace back.Core.Application.Services
{
    public class DictionaryService : IDictionaryService
    {
        private IDictionaryRepository repository;
        private IMapper mapper;
        public DictionaryService(IDictionaryRepository repository, IMapper mapper)
        {
            this.repository = repository;
            this.mapper = mapper;
        }

        public async Task<List<DictionaryDto>> GetDictionariesAsync(GetDictionariesRequest request)
        {
            return await repository.GetDictionariesAsync(request.Take, request.LastId ,request.TitlePattern, 
                request.LabelsPool, request.LangFromPool, request.LangToPool, 
                request.DateCreatedFrom, request.DateCreatedTo);
        }

        public async Task<List<DictionaryDto>> GetUsersDictionariesAsync(int userId)
        {
            return await repository.GetUsersDictionariesAsync(userId);
        }

        public async Task<List<DictionaryDto>> GetUsersAvailableDictionariesAsync(int userId)
        {
            return await repository.GetUsersAvailableDictionariesAsync(userId);
        }

        public async Task<bool> IsUserCreator(int userId, int dictionaryId)
        {
            return await repository.IsUserCreator(userId, dictionaryId);
        }

        public async Task<DictionaryExtended?> GetFullDictionaryAsync(int dictionaryId)
        {
            return await repository.GetFullDictionaryAsync(dictionaryId);
        }

        public async Task<bool> UpdateDictionaryWithCardsAsync(int dictionaryId,
            string newTitle, string newDescription,
            bool IsPublic,
            (string from, string to) langsChange,
            string newLabel,
            string? CEFR,
            List<CardFromClient> cardsToInsert,
            List<CardToClient> cardsToUpdate,
            List<int> cardsToDelete,
            AIСorrectnessService сorrectnessChecker)
        {
            // если было смена языка, с фронта в cardsToUpdate должны приходить все слова, даже те которые не трогали
            Task<(List<CardToClient>? update, List<CardToClient>? insert)>? checkedCards = null;
            List<CardToClient> cardsToInsertExtended = mapper.Map<List<CardToClient>>(cardsToInsert);

            if (IsPublic && (cardsToUpdate.Any() || cardsToInsert.Any()))
            {
                checkedCards = сorrectnessChecker.CheckUpdateAsync(cardsToUpdate, cardsToInsert,
                    (langsChange.from, langsChange.to));
            }
            else if (!IsPublic)
            {
                if (cardsToInsertExtended.Any())
                {
                    foreach (var card in cardsToInsertExtended)
                        card.Status = "Private";
                }
                if (cardsToUpdate.Any())
                {
                    foreach (var card in cardsToUpdate)
                        card.Status = "Private";
                }
            }

            if (checkedCards is not null)
            {
                (List<CardToClient>? update, List<CardToClient>? insert) = await checkedCards;
                cardsToUpdate = update;
                cardsToInsertExtended = insert;
            }

            bool result = await repository.UpdateDictionaryWithCardsAsync(
                dictionaryId, newTitle, newDescription, langsChange, newLabel, CEFR,
                cardsToInsertExtended, cardsToUpdate, cardsToDelete);
            
            return result;
        }

        public async Task<bool> InsertDictionaryWithCardsAsync(DictionaryFromClient dictionary,
            int creatorId, AIСorrectnessService сorrectnessChecker)
        {
            List<CardToClient> checkedCards;
            if (!dictionary.IsPublic)
            {
                checkedCards = mapper.Map<List<CardToClient>>(dictionary.Cards);
                foreach (var card in checkedCards)
                    card.Status = "Private";
            }
            else
            {
                checkedCards = await сorrectnessChecker.CheckInsertAsync(
                    dictionary.Cards, (dictionary.FromLang, dictionary.ToLang));
            }

            bool result = await repository.InsertDictionaryWithCardsAsync(dictionary, checkedCards, creatorId);

            return result;
        }

        public async Task<bool> DeleteDictionaryWithCardsAsync(int dictionaryId)
        {
            return await repository.DeleteDictionaryWithCardsAsync(dictionaryId);
        }

        public async Task<bool> ChangeDictionaryVisibility(int dictionaryId, AIСorrectnessService сorrectnessChecker)
        {
            DictionaryExtended? dictionary = await repository.GetFullDictionaryAsync(dictionaryId);

            if(dictionary is null)
                return false;

            if (dictionary.IsPublic)
            {
                await repository.MakeDictionaryPrivateAsync(dictionaryId);
            }
            else
            {
                IEnumerable<CardToClient> cardsToCheck = dictionary.Cards.Where(el => el.Status == "Private");

                var checkedCards = await сorrectnessChecker.CheckUpdateAsync(cardsToCheck.ToList(), null, (dictionary.FromLang, dictionary.ToLang));

                await repository.MakeDictionaryPublicAsync(dictionaryId, checkedCards.update!);
            }

            return true;
        }
    }
}
