from django.http import HttpResponse
from rest_framework.decorators import api_view
from . import model

import logging
logger = logging.getLogger("mylogger")

@api_view(['POST'])
def guess(request):
    array = request.data
    return HttpResponse(model.predict(array))