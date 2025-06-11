using back.Core.Domain.Models;
using back.Core.Domain.Records;
using back.Core.Application.Business_Entities;
using back.Core.Application.Logic.Answers;

namespace back.Core.Application.Logic.Questions
{
    public class MultipleChoiceQuestionGenerator : IQuestionGenerator
    {
        private readonly int lowestPossibleAnswersNumber = 3;
        public List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards)
        {
            List<MultipleChoiceQuestion> multipleChoiceQ = new();

            var cardsWithTranslation = totalCards.Where(card => card.Translation is not null).ToList();
            var cardsWithMeaning = totalCards.Where(card => card.Meaning is not null).ToList();

            Random rnd = new();
            for (int i = 0; i < amount; i++)
            {
                Card card = remainingCardsToUse[rnd.Next(0, remainingCardsToUse.Count)];
                MultipleChoiceQuestion question = new();

                if (card.Translation is not null && card.Meaning is not null)
                {
                    question.Direction = (QDirection)rnd.Next(1, 5); // 25%
                }
                else if (card.Translation is null)
                {
                    question.Direction = rnd.NextDouble() > 0.5 // 50%
                        ? QDirection.TermToMeaning
                        : QDirection.MeaningToTerm;
                }
                else
                {
                    question.Direction = rnd.NextDouble() > 0.5 // 50%
                        ? QDirection.TermToTranslation
                        : QDirection.TranslationToTerm;
                }

                // в случае если в сджоиненых словарях просто нету достаточного количества карт (4)
                // с переводом / определением - надо понижать количество ответов в вопросе
                int answersAmount = cardsWithMeaning.Count == lowestPossibleAnswersNumber
                    || cardsWithTranslation.Count == lowestPossibleAnswersNumber
                    ? lowestPossibleAnswersNumber : 4;

                switch (question.Direction)
                {
                    case QDirection.TermToTranslation:
                        new TermToTranslationAnswerGenerator().Generate(new List<Card>(cardsWithTranslation), card, question, answersAmount, rnd);
                        break;
                    case QDirection.TranslationToTerm:
                        new TranslationToTermAnswerGenerator().Generate(new List<Card>(cardsWithTranslation), card, question, answersAmount, rnd);
                        break;
                    case QDirection.TermToMeaning:
                        new TermToMeaningAnswerGenerator().Generate(new List<Card>(cardsWithMeaning), card, question, answersAmount, rnd);
                        break;
                    case QDirection.MeaningToTerm:
                        new MeaningToTermAnswerGenerator().Generate(new List<Card>(cardsWithMeaning), card, question, answersAmount, rnd);
                        break;
                }


                multipleChoiceQ.Add(question);
                remainingCardsToUse.Remove(card);
            }
            return multipleChoiceQ.Cast<Question>().ToList();
        }
    }
}
