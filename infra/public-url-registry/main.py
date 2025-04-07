import time
import requests

NGROK_API = "http://ngrok:4040/api/tunnels"

def wait_for_ngrok():
    for _ in range(30):
        try:
            response = requests.get(NGROK_API)
            if response.status_code == 200:
                return response.json()
        except Exception:
            pass
        time.sleep(2)
    raise Exception("Ngrok API did not become available")

def main():
    data = wait_for_ngrok()
    public_url = data["tunnels"][0]["public_url"]
    print("Ngrok public URL:", public_url)

    response = requests.post(public_url + "/internal/webhook-url", json={"url": public_url})
    print("Response from app:", response.status_code, response.text)

if __name__ == "__main__":
    main()