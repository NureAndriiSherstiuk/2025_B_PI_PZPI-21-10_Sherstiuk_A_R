using AutoMapper;
using back.Core.Domain.Records;
using Microsoft.AspNetCore.Mvc;
using back.Core.Application.Services;
using back.Core.Application.Logic;
using back.Core.Application.Business_Entities;
using back.API.Requests;


namespace back.API.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TestController : ControllerBase
    {
        private readonly TestService testService;
        private IMapper mapper;

        public TestController(TestService testService, IMapper mapper)
        {
            this.testService = testService;
            this.mapper = mapper;
        }

        [HttpPost]
        public async Task<IActionResult> GetTest([FromBody] GetTestRequest request)
        {
            Test test;
            try
            {
                test = await testService.GenerateTestAsync(
                request.questionsNumber, request.questionTypes, request.dictionariesId);
            }
            catch (Exception ex)
            {
                return BadRequest(new { Error = $"{ex.Message}" });
            }

            return Ok(test);
        }
    }
}
