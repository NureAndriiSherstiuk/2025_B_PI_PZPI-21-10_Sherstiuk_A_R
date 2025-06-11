using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Localization;
using System.ComponentModel.DataAnnotations;
using System.Net;
using System.Text.Json;

namespace back.API.MIddlewares
{
    public class GlobalErrorHandlerMiddleware
    {
        private readonly RequestDelegate _next;

        public GlobalErrorHandlerMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception ex)
            {
                await HandleExceptionAsync(context, ex);
            }
        }

        private async Task HandleExceptionAsync(HttpContext context, Exception ex)
        {
            HttpStatusCode code = HttpStatusCode.InternalServerError;

            switch (ex)
            {
                case ValidationException e:
                    code = HttpStatusCode.BadRequest;
                    break;
                case KeyNotFoundException:
                    code = HttpStatusCode.BadRequest;
                    break;
                case InvalidOperationException:
                    code = HttpStatusCode.BadRequest;
                    break;
                case ArgumentException:
                    code = HttpStatusCode.BadRequest;
                    break;
                case SqlException:
                    code = HttpStatusCode.BadRequest;
                    break;
                default:
                    code = HttpStatusCode.InternalServerError;
                    break;
            }

            context.Response.ContentType = "application/json";
            context.Response.StatusCode = (int)code;

            await JsonSerializer.SerializeAsync(context.Response.Body, new {Error = ex.Message});
        }
    }
}
