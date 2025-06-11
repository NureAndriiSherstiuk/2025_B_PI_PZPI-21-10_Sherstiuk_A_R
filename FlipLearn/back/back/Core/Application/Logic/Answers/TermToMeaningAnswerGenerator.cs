using back.Core.Application.Business_Entities;
using back.Core.Domain.Models;

namespace back.Core.Application.Logic.Answers
{
    public class TermToMeaningAnswerGenerator : IMultipleChoiceAnswerGenerator
    {
        public void Generate(List<Card> cardsWithMeaningCopy, Card card, MultipleChoiceQuestion question, int aAmount, Random rnd)
        {
            //обязательно убрать целевую карточку чтобы он рандомно не выбрал её как один из вариатов ответа
            cardsWithMeaningCopy.Remove(card);

            question.Term = card.Term;
            question.Answers = [];

            for (int i = 0; i < aAmount; i++)
                question.Answers.Add(new MultipleChoiceAnswer() { IsCorrect = false });

            List<int> answersIndexes = Enumerable.Range(0, aAmount).ToList();

            int correctAnswerPosition = rnd.Next(0, aAmount);
            question.Answers[correctAnswerPosition].IsCorrect = true;
            question.Answers[correctAnswerPosition].Text = card.Meaning;
            answersIndexes.Remove(correctAnswerPosition);

            for (int j = 0; j < aAmount - 1; j++)
            {
                Card tempCard = cardsWithMeaningCopy[rnd.Next(0, cardsWithMeaningCopy.Count)];
                int randomIndex = answersIndexes[rnd.Next(0, answersIndexes.Count)];
                question.Answers[randomIndex].Text = tempCard.Meaning;
                answersIndexes.Remove(randomIndex);
                cardsWithMeaningCopy.Remove(tempCard);
            }
        }
    }
}
