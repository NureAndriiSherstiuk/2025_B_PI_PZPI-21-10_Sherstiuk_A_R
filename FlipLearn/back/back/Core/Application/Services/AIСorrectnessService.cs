using back.Core.Application.Business_Entities;
using Ollama.Core.Models;
using Ollama.Core;
using System.Text.Json;
using AutoMapper;
using back.Core.Domain.DTO;

namespace back.Core.Application.Services
{
    public class AIСorrectnessService
    {
        private IMapper mapper;
        public AIСorrectnessService(IMapper mapper)
        {
            this.mapper = mapper;
        }

        private const int cardsAmountToGoParallel = 20;
        private const int parallelismDegree = 2;

        private static string PromptsLocation = Directory.GetCurrentDirectory() + "\\Properties\\AiPrompts\\";

        private static string FirstUserPrompt = File.ReadAllText(PromptsLocation + "FirstUserPrompt.txt");
        private static string FirstAIAnswer = File.ReadAllText(PromptsLocation + "FirstAIAnswer.txt");
        private static string SecondUserPrompt = File.ReadAllText(PromptsLocation + "SecondUserPrompt.txt");
        private static string SecondAIAnswer = File.ReadAllText(PromptsLocation + "SecondAIAnswer.txt");

        
        public async Task<(List<CardToClient>? update, List<CardToClient>? insert)> CheckUpdateAsync(List<CardToClient>? cardsToUpdate, 
            List<CardFromClient>? cardsToInsert,
            (string langFrom, string langTo) langPair)
        {
            List<CardAIFormat> cardsForAI = FormatCardsState(cardsToUpdate, cardsToInsert);

            List<Task<Task<List<AIResponse>>>> jobs = [];
            
            if (cardsForAI.Count > cardsAmountToGoParallel)
            {
                for (int i = 0; i < parallelismDegree; i++)
                    jobs.Add(GetJob(cardsForAI, langPair, i));
            }
            else
            {
                jobs.Add(GetJob(cardsForAI, langPair));
            }
            foreach (var job in jobs)
                job.Start();

            Task t = Task.WhenAll(jobs);
            await t;

            List<AIResponse> aIResponse = new();

            foreach (var job in jobs)
                aIResponse.AddRange(job.Result.Result);

            return RestoreCardsState(cardsToUpdate, cardsToInsert, aIResponse);

        }

        public async Task<List<CardToClient>> CheckInsertAsync(
            List<CardFromClient> cardsToInsert,
            (string langFrom, string langTo) langPair)
        {
            List<CardAIFormat> cardsForAI = mapper.Map<List<CardAIFormat>>(cardsToInsert);

            List<Task<Task<List<AIResponse>>>> jobs = [];

            if (cardsForAI.Count > cardsAmountToGoParallel)
            {
                for (int i = 0; i < parallelismDegree; i++)
                    jobs.Add(GetJob(cardsForAI, langPair, i));
            }
            else
            {
                jobs.Add(GetJob(cardsForAI, langPair));
            }
            foreach (var job in jobs)
                job.Start();

            Task t = Task.WhenAll(jobs);
            await t;

            List<AIResponse> aIResponse = new();

            foreach (var job in jobs)
                aIResponse.AddRange(job.Result.Result);

            var cardsToInsertExtended = mapper.Map<List<CardToClient>>(cardsToInsert);

            SetCardsState(cardsToInsertExtended, aIResponse);

            return cardsToInsertExtended;
        }

        private static Task<Task<List<AIResponse>>> GetJob(List<CardAIFormat> cards, 
            (string langFrom, string langTo) langPair, int iteration = -1)
        {
            Task<Task<List<AIResponse>>> job = new(async () =>
            {
                OllamaClient client = new("http://localhost:11434");

                ChatMessageHistory messages = [];
                messages.AddUserMessage(FirstUserPrompt);
                messages.AddAssistantMessage(FirstAIAnswer);
                messages.AddUserMessage(SecondUserPrompt);
                messages.AddAssistantMessage(SecondAIAnswer);

                if (iteration == -1)
                {
                    messages.AddUserMessage( $"Language pair ({langPair.langFrom}, {langPair.langTo})  " +
                        JsonSerializer.Serialize(cards) +
                    " Remember you only have to answer in a valid json, nothing else");
                }
                else
                {
                    int midIndex = cards.Count / 2;
                    List<CardAIFormat> chunk = iteration == 0 ? cards.GetRange(0, midIndex)
                        : cards.GetRange(midIndex, cards.Count - midIndex);

                    messages.AddUserMessage($"Language pair ({langPair.langFrom}, {langPair.langTo})  " +
                        JsonSerializer.Serialize(chunk) +
                    " Remember you only have to answer in a valid json, nothing else");
                }

                ChatCompletionResponse response = await client.ChatCompletionAsync("llama3.1", messages);
                Console.WriteLine(response.Message.Content);

                var options = new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                };

                return JsonSerializer.Deserialize<List<AIResponse>>(response.Message.Content, options);
            });
            return job;
        }

        private (List<CardToClient>? update, List<CardToClient>? insert) RestoreCardsState(
            List<CardToClient>? cardsToUpdate, List<CardFromClient>? cardsToInsert, 
            List<AIResponse> aIResponse)
        { 
            // если в списке будут две карточки с одним и тем же словом то обоим на
            // выходе будет присвоен статус первой попавшейся. вторая будет проигнорирована
            if(cardsToUpdate is not null)
                SetCardsState(cardsToUpdate, aIResponse);
            
            var cardsToInsertExtended = mapper.Map<List<CardToClient>?>(cardsToInsert);

            if(cardsToInsertExtended is not null)
                SetCardsState(cardsToInsertExtended, aIResponse);
            
            return (cardsToUpdate, cardsToInsertExtended);
        }

        private void SetCardsState(List<CardToClient> cards, List<AIResponse> aIResponse)
        {
            foreach (var card in cards)
            {
                AIResponse? response = aIResponse.FirstOrDefault(obj => obj.Word == card.Term);

                card.Status = response is not null &&
                    (response.Translation?.ToLower() == "yes" || response.Definition?.ToLower() == "yes")
                    ? "Confirmed" : "Controversial";
            }
        }
        

        private List<CardAIFormat> FormatCardsState(List<CardToClient>? cardsToUpdate,
            List<CardFromClient>? cardsToInsert)
        {
            if (cardsToUpdate is null) 
            {
                return mapper.Map<List<CardAIFormat>>(cardsToInsert);
            }
            else if(cardsToInsert is null)
            {
                return mapper.Map<List<CardAIFormat>>(cardsToUpdate);
            }
            else
            {
                List<CardToClient> cardsToInsertWithNewFormat = mapper.Map<List<CardToClient>>(cardsToInsert);
                cardsToUpdate.AddRange(cardsToInsertWithNewFormat);
                return mapper.Map<List<CardAIFormat>>(cardsToUpdate);
            }
        }

        private class AIResponse
        {
            public string Word { get; set; }
            public string? Translation { get; set; }
            public string? Definition { get; set; }
        }
        
    }
}
