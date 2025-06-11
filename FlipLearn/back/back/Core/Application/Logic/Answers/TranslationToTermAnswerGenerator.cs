using back.Core.Application.Business_Entities;
using back.Core.Domain.Models;

namespace back.Core.Application.Logic.Answers
{
    public class TranslationToTermAnswerGenerator : IMultipleChoiceAnswerGenerator
    {
        public void Generate(List<Card> cardsWithTranslationCopy, Card card, MultipleChoiceQuestion question, int aAmount, Random rnd)
        {
            //обязательно убрать целевую карточку чтобы он рандомно не выбрал её как один из вариатов ответа
            cardsWithTranslationCopy.Remove(card);

            question.Translation = card.Translation;
            question.Answers = [];

            for (int i = 0; i < aAmount; i++)
                question.Answers.Add(new MultipleChoiceAnswer() { IsCorrect = false });

            List<int> answersIndexes = Enumerable.Range(0, aAmount).ToList();

            int correctAnswerPosition = rnd.Next(0, aAmount);
            question.Answers[correctAnswerPosition].IsCorrect = true;
            question.Answers[correctAnswerPosition].Text = card.Term;
            answersIndexes.Remove(correctAnswerPosition);

            for (int j = 0; j < aAmount - 1; j++)
            {
                Card tempCard = cardsWithTranslationCopy[rnd.Next(0, cardsWithTranslationCopy.Count)];
                int randomIndex = answersIndexes[rnd.Next(0, answersIndexes.Count)];
                question.Answers[randomIndex].Text = tempCard.Term;
                answersIndexes.Remove(randomIndex);
                cardsWithTranslationCopy.Remove(tempCard);
            }
        }
    }
}
