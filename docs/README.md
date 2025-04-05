# Como obter seu token de autenticação do ngrok

Para que o serviço do Ngrok funcione corretamente em modo autenticado (com domínio estável e confiável), você precisará do `NGROK_AUTHTOKEN`. Siga os passos abaixo:

1. Acesse o site do [Ngrok](https://ngrok.com/) e clique em "Sign Up" para criar uma conta gratuita (ou "Log In" se já tiver uma).
2. Após logar, vá até o seu [Dashboard](https://dashboard.ngrok.com/get-started/your-authtoken).
3. Copie o valor exibido na seção **"Your Authtoken"**.
4. Utilize esse token ao iniciar o container do Ngrok:

   ```bash
   NGROK_AUTHTOKEN=seu_token docker compose -f infra/docker-compose.yml up -d ngrok --no-deps
