using AutoMapper;
using back.Core.Domain.Models;
using back.Core.Domain.Repositories;
using back.Core.Application.Logic;
using back.Core.Application.Business_Entities;
using back.Core.Domain.Records;
using back.Core.Application.Logic.Questions;
using back.Core.Application.Services.Interfaces;

namespace back.Core.Application.Services
{
    public class TestService : ITestService
    {
        private IDictionaryRepository dictionaryRep;
        private IMapper mapper;
        public TestService(IDictionaryRepository _dictionaryRep, IMapper mapper)
        {
            dictionaryRep = _dictionaryRep;
            this.mapper = mapper;
        }

        public async Task<Test> GenerateTestAsync(int questionsNumber, List<int> questionTypes,
            List<int> dictionariesId)
        {
            List<IQuestionGenerator> generators = new();

            foreach (int questionType in questionTypes)
                generators.Add(QuestionGeneratorFactory.GetGenerator((QTypes)questionType));

            List<Dictionary> dictionaries = await dictionaryRep.GetFullDictionariesAsync(dictionariesId);

            Test test;
            try
            {
                test = TestGenerator.GenerateSimpleTest(questionsNumber, generators, dictionaries, mapper);
            }
            catch
            {
                throw;
            }
            return test;
        }
    }
}
