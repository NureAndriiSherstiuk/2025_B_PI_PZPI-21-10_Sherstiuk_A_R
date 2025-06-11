using back.Core.Application.Business_Entities;

namespace back.Core.Application.Services.Interfaces
{
    public interface ITestService
    {
        Task<Test> GenerateTestAsync(int questionsNumber, List<int> questionTypes,
            List<int> dictionariesId);
    }
}
