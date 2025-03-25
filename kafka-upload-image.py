from kafka import KafkaProducer
import json
import base64

userId = '67e1f0b5c4c6a004b08189eb'
tokenUser = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0YXdhbi5zb3V6YUBiaXguY29tIiwidXNlciI6IntcImlkXCI6XCI2N2UxZjBiNWM0YzZhMDA0YjA4MTg5ZWJcIixcIm5hbWVcIjpcIlRhd2FuXCIsXCJlbWFpbFwiOlwidGF3YW4uc291emFAYml4LmNvbVwifSIsImF1dGhvcml0aWVzIjoiUExBTl9CQVNJQyIsImlhdCI6MTc0Mjg2MDkyMiwiZXhwIjoxNzQzNDY1NzIyfQ.t6f16xFOsCld33dontdLWXIIJtxSA9ATrCle107ZHbs'


topic = 'SAVE_IMAGE_TOPIC'
host = 'localhost:9092'
producer = KafkaProducer(
  bootstrap_servers=[host],
  value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

filename = 'porsche-911-gt3.jpeg'
with open(filename, "rb") as image:
  file = image.read()
  arr_bytes = bytearray(file)


encoded_bytes = base64.b64encode(arr_bytes).decode('utf-8')


data = {
  'name': filename,
  'userId': userId,
  'token': tokenUser,
  'imageData': {'data': encoded_bytes}
}


producer.send(topic, value=data)
producer.flush()
producer.close()