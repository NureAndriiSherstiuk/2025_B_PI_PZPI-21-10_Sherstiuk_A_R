using back.Core.Domain.Models;
using back.Core.Domain.Records;
using back.Core.Application.Business_Entities;

namespace back.Core.Application.Logic.Questions
{
    public class HandwrittenQuestionGenerator : IQuestionGenerator
    {
        public List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards)
        {
            // вопросы с переводом в приоритете, если их мало и до нужного кол-ва не достаёт добавляем определения 
            List<HandwrittenQuestion> handwrittenQ = new();

            Random rnd = new();
            while (amount > 0)
            {
                Card card = remainingCardsToUse[rnd.Next(0, remainingCardsToUse.Count)];
                HandwrittenQuestion hwq = new();
                if (card.Translation is not null)
                {
                    // случайное направление вопроса, либо TermToTranslation либо TranslationToTerm
                    if (rnd.NextDouble() > 0.5)
                    {
                        hwq.Direction = QDirection.TermToTranslation;
                        hwq.Term = card.Term;
                        hwq.Answer = new HandwrittenAnswer() { IsCorrect = true, CorrectInput = card.Translation };
                    }
                    else
                    {
                        hwq.Direction = QDirection.TranslationToTerm;
                        hwq.Translation = card.Translation;
                        hwq.Answer = new HandwrittenAnswer() { IsCorrect = true, CorrectInput = card.Term };
                    }
                }
                else
                {
                    // тут только MeaningToTerm потому что в обратном направлении может быть очень трудно юзеру правильно расписать
                    hwq.Direction = QDirection.MeaningToTerm;
                    hwq.Meaning = card.Meaning;
                    hwq.Answer = new HandwrittenAnswer() { IsCorrect = true, CorrectInput = card.Term };
                }
                handwrittenQ.Add(hwq);
                remainingCardsToUse.Remove(card);
                amount--;
            }
            return handwrittenQ.Cast<Question>().ToList();
        }
    }
}
