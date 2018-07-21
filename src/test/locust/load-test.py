from locust import HttpLocust, TaskSet, task
from time import time
import json
import random

class UserBehavior(TaskSet):

    @task(20)
    def transactions(self):
        payload = {'timestamp': int(time() * 1000 - random.randrange(0, 5000)), 'amount': float("{0:.2f}".format(random.uniform(0, 50))) }
        headers = {'content-type': 'application/json'}
        self.client.post("/transactions", data=json.dumps(payload), headers=headers)

    @task(1)
    def statistics(self):
        response = self.client.get("/statistics")
        print "Statistics:", response.content

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 100
    max_wait = 1000
