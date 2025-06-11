using back.Core.Application.Business_Entities;
using back.Core.Domain.Models;


namespace back.Core.Application.Logic.Answers
{
    public interface IMultipleChoiceAnswerGenerator
    {
        void Generate(List<Card> cardsToSelectFrom, Card card,
            MultipleChoiceQuestion question, int aAmount, Random rnd);
    }
}
