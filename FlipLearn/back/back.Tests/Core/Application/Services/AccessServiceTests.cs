using Autofac.Extras.Moq;
using back.Core.Domain.Repositories;
using back.Core.Domain.Records;
using Moq;
using back.Core.Application.Services;

namespace back.Tests.Core.Application.Services
{
    public class AccessServiceTests
    {
        [Fact]
        public async Task GetUserPermission_ValidResult()
        {
            using var mock = AutoMock.GetLoose();

            mock.Mock<IAccessRepository>()
                .Setup(m => m.GetUserPermission(1, 2))
                .ReturnsAsync("CoAuthor");

            AccessService service = mock.Create<AccessService>();

            Access expected = Access.CoAuthor;
            Access actual = await  service.GetUserPermission(1, 2);

            Assert.Equal(expected, actual);
        }
    }
}
