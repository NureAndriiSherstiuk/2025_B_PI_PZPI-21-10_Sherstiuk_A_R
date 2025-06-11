using back.Core.Application.Business_Entities;
using back.Core.Domain.Models;
using back.Core.Domain.Records;

namespace back.Core.Application.Logic.Questions
{
    public class VoiceQuestionGenerator : IQuestionGenerator
    {
        public List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards)
        {
            // вопросы с переводом в приоритете, если их мало и до нужного кол-ва не достаёт добавляем определения 
            List<VoiceQuestion> voiceQ = new();

            Random rnd = new();
            while (amount > 0)
            {
                Card card = remainingCardsToUse[rnd.Next(0, remainingCardsToUse.Count)];
                VoiceQuestion vq = new();
                if (card.Translation is not null)
                {
                    // случайное направление вопроса, либо TermToTranslation либо TranslationToTerm
                    if (rnd.NextDouble() > 0.5)
                    {
                        vq.Direction = QDirection.TermToTranslation;
                        vq.Term = card.Term;
                        vq.Answer = new VoiceAnswer() { IsCorrect = true, CorrectAnswer = card.Translation };
                    }
                    else
                    {
                        vq.Direction = QDirection.TranslationToTerm;
                        vq.Translation = card.Translation;
                        vq.Answer = new VoiceAnswer() { IsCorrect = true, CorrectAnswer = card.Term };
                    }
                }
                else
                {
                    // тут только MeaningToTerm потому что в обратном направлении может быть очень трудно юзеру правильно расписать
                    vq.Direction = QDirection.MeaningToTerm;
                    vq.Meaning = card.Meaning;
                    vq.Answer = new VoiceAnswer() { IsCorrect = true, CorrectAnswer = card.Term };
                }
                voiceQ.Add(vq);
                remainingCardsToUse.Remove(card);
                amount--;
            }
            return voiceQ.Cast<Question>().ToList();
        }
    }
}
