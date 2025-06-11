using back.Core.Domain.Models;
using back.Core.Domain.Records;
using back.Core.Application.Business_Entities;

namespace back.Core.Application.Logic.Questions
{
    public class TrueFalseQuestionGenerator : IQuestionGenerator
    {
        public List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards)
        {
            List<TrueFalseQuestion> trueFalseQ = new();

            var cardsWithTranslation = totalCards.Where(card => card.Translation is not null).ToList();
            var cardsWithMeaning = totalCards.Where(card => card.Meaning is not null).ToList();

            Random rnd = new();
            for (int i = 0; i < amount; i++)
            {
                Card card = remainingCardsToUse[rnd.Next(0, remainingCardsToUse.Count)];
                TrueFalseQuestion question = new();
                question.Term = card.Term;

                if (rnd.NextDouble() > 0.5)
                {
                    // вопрос с правильным ответом в самом вопросе
                    if (card.Translation is not null
                        && card.Meaning is not null)
                    {
                        if (rnd.NextDouble() > 0.5)
                        {
                            question.Meaning = card.Meaning;
                            question.Direction = QDirection.TermToMeaning;
                        }
                        else
                        {
                            question.Translation = card.Translation;
                            question.Direction = QDirection.TermToTranslation;
                        }
                    }
                    else if (card.Translation is null)
                    {
                        question.Meaning = card.Meaning;
                        question.Direction = QDirection.MeaningToTerm;
                    }
                    else
                    {
                        question.Translation = card.Translation;
                        question.Direction = QDirection.TranslationToTerm;
                    }
                    question.Answers = [
                        new TrueFalseAnswer() { Choice = true, IsCorrect = true},
                        new TrueFalseAnswer() { Choice = false, IsCorrect = false}
                        ];
                }
                else
                {
                    // вопрос с неправильным ответом в самом вопросе
                    if (card.Translation is not null
                        && card.Meaning is not null)
                    {
                        if (rnd.NextDouble() > 0.5)
                        {
                            cardsWithMeaning.Remove(card);
                            question.Meaning = cardsWithMeaning[rnd.Next(0, cardsWithMeaning.Count)].Meaning;
                            question.Direction = QDirection.TermToMeaning;
                            cardsWithMeaning.Add(card);
                        }
                        else
                        {
                            cardsWithTranslation.Remove(card);
                            question.Translation = cardsWithTranslation[rnd.Next(0, cardsWithTranslation.Count)].Translation; ;
                            question.Direction = QDirection.TermToTranslation;
                            cardsWithTranslation.Add(card);
                        }
                    }
                    else if (card.Translation is null)
                    {
                        cardsWithMeaning.Remove(card);
                        question.Meaning = cardsWithMeaning[rnd.Next(0, cardsWithMeaning.Count)].Meaning;
                        question.Direction = QDirection.MeaningToTerm;
                        cardsWithMeaning.Add(card);
                    }
                    else
                    {
                        cardsWithTranslation.Remove(card);
                        question.Translation = cardsWithTranslation[rnd.Next(0, cardsWithTranslation.Count)].Translation;
                        question.Direction = QDirection.TranslationToTerm;
                        cardsWithTranslation.Add(card);
                    }
                    question.Answers = [
                        new TrueFalseAnswer() { Choice = true, IsCorrect = false},
                        new TrueFalseAnswer() { Choice = false, IsCorrect = true}
                        ];
                }
                trueFalseQ.Add(question);
                remainingCardsToUse.Remove(card);
            }
            return trueFalseQ.Cast<Question>().ToList();
        }
    }
}
