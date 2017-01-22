import json
import sys
import requests


params = {'callbackURI': 'http://localhost:5000/callback',
          'numberA': 123,
          'numberB': 321}

headers = {'content-type': 'application/json'}


if len(sys.argv) > 2:
    params['numberA'] = sys.argv[1]
    params['numberB'] = sys.argv[2]


r = requests.post('http://127.0.0.1:8080/RestConnector/job',data=json.dumps(params), headers=headers)
print r.text
