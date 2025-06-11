using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using Ollama.Core.Models;
using Ollama.Core;
using System.Text;
using back.Infrastructure.Persistance.Repositories;
using back.Infrastructure.Persistance.DbConnections;
using back.Core.Domain.Repositories;
using back.Core.Application.Services;
using back.API.Hubs;
using back.API.MIddlewares;
using FluentValidation.AspNetCore;
using back.API.Validators;
using FluentValidation;
using System;
using Microsoft.AspNetCore.Identity.Data;
using back.API.Requests;
using MongoDB.Driver;
using static Dapper.SqlMapper;
using MongoDB.Bson;

namespace back
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options =>
                {
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateIssuer = true,
                        ValidIssuer = Environment.GetEnvironmentVariable("Auth-Issuer"),
                        ValidateAudience = true,
                        ValidAudience = Environment.GetEnvironmentVariable("Auth-Audience"),
                        ValidateLifetime = true,
                        IssuerSigningKey = new SymmetricSecurityKey(
                            Encoding.UTF8.GetBytes(Environment.GetEnvironmentVariable("Auth-Key"))
                            ),

                        ValidateIssuerSigningKey = true,
                    };
                    options.Events = new JwtBearerEvents
                    {
                        OnMessageReceived = context =>
                        {
                            var accessToken = context.Request.Query["access_token"];
                            var path = context.HttpContext.Request.Path;

                            if (!string.IsNullOrEmpty(accessToken) &&
                                (path.StartsWithSegments("/rooms") || path.StartsWithSegments("/room")))
                            {
                                context.Token = accessToken;
                            }

                            return Task.CompletedTask;
                        }
                    };
                });
            builder.Services.AddAuthorization();

            builder.Services.AddControllers();
            
            builder.Services.AddFluentValidationAutoValidation();
            builder.Services.AddScoped<IValidator<RegistrationRequest>, RegistrationRequestValidator>();
            builder.Services.AddScoped<IValidator<ChangePasswordRequest>, ChangePasswordRequestValidator>();

            builder.Services.AddEndpointsApiExplorer();

            builder.Services.AddSwaggerGen(options =>
            {
                options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme()
                {
                    Description = "Please enter token",
                    Name = "Authorization",
                    In = ParameterLocation.Header,
                    Scheme = "Bearer",
                    BearerFormat = "JWT",
                    Type = SecuritySchemeType.Http
                });

                options.AddSecurityRequirement(new OpenApiSecurityRequirement()
                {
                    {
                        new OpenApiSecurityScheme()
                        {
                            Reference = new OpenApiReference()
                            {
                                Type = ReferenceType.SecurityScheme,
                                Id = "Bearer"
                            }
                        },
                        new List<string>()
                    }
                });
            });

            builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
            builder.Services.AddSignalR(options =>
            {
                options.DisableImplicitFromServicesParameters = true;
                options.EnableDetailedErrors = true;
            });

            builder.Services.AddScoped<IUserRepository, UserRepository>();
            builder.Services.AddScoped<UserService>();

            builder.Services.AddScoped<IDictionaryRepository, DictionaryRepository>();
            builder.Services.AddScoped<DictionaryService>();

            builder.Services.AddScoped<IAccessRepository, AccessRepository>();
            builder.Services.AddScoped<AccessService>();

            builder.Services.AddScoped<IEmailCodeRepository, EmailCodeRepository>();
            builder.Services.AddScoped<EmailCodeService>();

            builder.Services.AddScoped<TestService>();

            builder.Services.AddScoped<AIÑorrectnessService>();
            builder.Services.AddTransient<RoomsService>();

            builder.Services.AddTransient<IRoomRepository, RoomRepository>();
            builder.Services.AddTransient<RoomService>();

            builder.Services.Configure<MsSQLConnectionOptions>(options =>
            {
                options.ConnectionString = Environment.GetEnvironmentVariable("DBConnection");
            });

            var mongoClient = new MongoClient(Environment.GetEnvironmentVariable("Mongo"));
            builder.Services.AddSingleton<IMongoClient>(mongoClient);
            builder.Services.AddScoped<MsSQLConnectionWrapper>();

            if (builder.Configuration.GetValue<bool>("AI:IsNecessary"))
            {
                OllamaClient client = new("http://localhost:11434");

                GenerateCompletionResponse response = await client.GenerateCompletionAsync("llama3.1",
                    builder.Configuration.GetValue<string>("AI:Warm-upRequest"));
                Console.WriteLine(response.Response);
            }

            var MyAllowSpecificOrigins = "_myAllowSpecificOrigins";

            builder.Services.AddCors(options =>
            {
                options.AddPolicy(name: MyAllowSpecificOrigins,
                                  policy =>
                                  {
                                      policy.WithOrigins("http://193.194.111.130:5173", "193.194.111.130", "http://localhost:5173")
                                            .AllowAnyMethod()
                                            .AllowAnyHeader()
                                            .AllowCredentials();
                                  });
            });


            var app = builder.Build();

            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseMiddleware<GlobalErrorHandlerMiddleware>();

            app.UseHttpsRedirection();
            app.UseCors(MyAllowSpecificOrigins);
            app.UseAuthentication();
            app.UseAuthorization();

            app.MapControllers();
            app.MapHub<RoomsHub>("rooms");
            app.MapHub<RoomHub>("room");

            app.Run();
        }
    }
}
