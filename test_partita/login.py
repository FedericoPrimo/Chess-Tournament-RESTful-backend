import requests

URLPLAYER = "http://localhost:8080/api/player/login"
URLSPECT = "http://localhost:8080/api/spectator/login"
URLMANAGER = "http://localhost:8080/api/manager/login"

def playerLogin(user):
  username = user
  password = user

  # Request to log the player
  headers = {"Content-Type": "application/json"}
  data = {"username": username, "password": password}
  response = requests.post(URLPLAYER, json=data, headers=headers)
  token = "Token not found"
  if response.ok:
    token = response.json().get("accessToken")
  
  return token

def spectatorLogin(user):
  username = user
  password = user

  # Request to log the player
  headers = {"Content-Type": "application/json"}
  data = {"username": username, "password": password}
  response = requests.post(URLSPECT, json=data, headers=headers)
  token = "Token not found"
  if response.ok:
    token = response.json().get("accessToken")
  
  return token

  
def managerLogin(user):
  username = user
  password = user

  # Request to log the player
  headers = {"Content-Type": "application/json"}
  data = {"username": username, "password": password}
  response = requests.post(URLSPECT, json=data, headers=headers)
  token = "Token not found"
  if response.ok:
    token = response.json().get("accessToken")
  
  return token