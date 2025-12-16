import uuid
import random
from datetime import timedelta, datetime

FILE_PATH = "data/input/transactions.csv"
FILE_NAME = "transactions.csv"
TOT_LINES = 5000000 

MIN_AMOUNT = 0.1
MAX_AMOUNT = 1000000.99

print(f"Gerando {TOT_LINES} linhas de transações em '{FILE_NAME}'...")

with open(FILE_PATH, 'w', encoding='utf-8') as file:
    file.write("clientId,amount,timeStamp\n")
    
    for i in range(1, TOT_LINES + 1):
        id = str(uuid.uuid4())
        
        # Gerar valor de montante aleatório
        amount = random.uniform(MIN_AMOUNT, MAX_AMOUNT)
        amount_str = f"${amount:.2f}"
        
        # Gerar timestamp aleatório 
        hour = random.randint(0, 23)
        minute = random.randint(0, 59)
        second = random.randint(0, 59)
        time_str = f"{hour:02d}:{minute:02d}:{second:02d}"
        
        # Escrever linha
        file.write(f"{id},{amount_str},{time_str}\n")
        
        # Mostrar progresso 
        if i % 100000 == 0:
            print(f"{i:,} linhas geradas...")

print(f"Concluído! Arquivo salvo como '{FILE_NAME}' com {TOT_LINES + 1:,} linhas.")