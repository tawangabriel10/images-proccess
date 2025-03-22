from kafka import KafkaProducer
import json

imageId = '67db9c36104f303fb5fd9928'
topic = 'PROCESS_IMAGE_TOPIC'
action = 'GRAY'

host = 'localhost:9092'
producer = KafkaProducer(
  bootstrap_servers=[host],
  value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

data = {
  'imageId': imageId,
  'action': action
}

producer.send(topic, value=data)
producer.flush()
producer.close()