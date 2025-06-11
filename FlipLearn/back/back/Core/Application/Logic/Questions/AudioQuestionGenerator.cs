using back.Core.Application.Business_Entities;
using back.Core.Domain.Models;
using back.Core.Domain.Records;

namespace back.Core.Application.Logic.Questions
{
    public class AudioQuestionGenerator : IQuestionGenerator
    {
        public List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards)
        {
            // вопросы с переводом в приоритете, если их мало и до нужного кол-ва не достаёт добавляем определения 
            List<AudioQuestion> audioQ = new();

            Random rnd = new();
            while (amount > 0)
            {
                Card card = remainingCardsToUse[rnd.Next(0, remainingCardsToUse.Count)];
                AudioQuestion hwq = new();
                if (card.Translation is not null)
                {
                    // случайное направление вопроса, либо TermToTranslation либо TranslationToTerm
                    if (rnd.NextDouble() > 0.5)
                    {
                        hwq.Direction = QDirection.TermToTranslation;
                        hwq.Term = card.Term;
                        hwq.Answer = new AudioAnswer() { IsCorrect = true, CorrectAnswer = card.Translation };
                    }
                    else
                    {
                        hwq.Direction = QDirection.TranslationToTerm;
                        hwq.Translation = card.Translation;
                        hwq.Answer = new AudioAnswer() { IsCorrect = true, CorrectAnswer = card.Term };
                    }
                }
                else
                {
                    // тут только MeaningToTerm потому что в обратном направлении может быть очень трудно юзеру правильно расписать
                    hwq.Direction = QDirection.MeaningToTerm;
                    hwq.Meaning = card.Meaning;
                    hwq.Answer = new AudioAnswer() { IsCorrect = true, CorrectAnswer = card.Term };
                }
                audioQ.Add(hwq);
                remainingCardsToUse.Remove(card);
                amount--;
            }
            return audioQ.Cast<Question>().ToList();
        }
    }
}
