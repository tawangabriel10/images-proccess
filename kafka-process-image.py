from kafka import KafkaProducer
import json

imageId = '67e1f42bc5e1371a31cd29f5'
topic = 'PROCESS_IMAGE_TOPIC'
action = 'NEGATIVE'
userId = '67e1f0b5c4c6a004b08189eb'
tokenUser = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0YXdhbi5zb3V6YUBiaXguY29tIiwidXNlciI6IntcImlkXCI6XCI2N2UxYzgzMzJmMmQzZjI2NDJhZjg3YTFcIixcIm5hbWVcIjpcIlRhd2FuXCIsXCJlbWFpbFwiOlwidGF3YW4uc291emFAYml4LmNvbVwifSIsImF1dGhvcml0aWVzIjoiUExBTl9CQVNJQyIsImlhdCI6MTc0Mjg1MDExNiwiZXhwIjoxNzQzNDU0OTE2fQ.CkQGyhPqEbVH9F-cKDdZMrBcJHKX9I0YERq3jrTrct8'


host = 'localhost:9092'
producer = KafkaProducer(
  bootstrap_servers=[host],
  value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

data = {
  'imageId': imageId,
  'action': action,
  'userId': userId,
  'token': tokenUser
}

producer.send(topic, value=data)
producer.flush()
producer.close()