from kafka import KafkaProducer
import json
import base64

userId = '67dae2788f1d36219db3bde1'
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
  'imageData': {'data': encoded_bytes}
}


producer.send(topic, value=data)
producer.flush()
producer.close()