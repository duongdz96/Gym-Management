import streamlit as st
import pandas as pd
import time 
from datetime import datetime
from database import get_logs_for_date

ts=time.time()
date=datetime.fromtimestamp(ts).strftime("%d-%m-%Y")
timestamp=datetime.fromtimestamp(ts).strftime("%H:%M-%S")

from streamlit_autorefresh import st_autorefresh

count = st_autorefresh(interval=2000, limit=100, key="refreshcounter")

logs = get_logs_for_date(date)
if logs:
    df = pd.DataFrame(logs, columns=['MEMBER_ID', 'ACCESS_TIME'])
    df['MEMBER_ID'] = df['MEMBER_ID'].astype(int)  # Convert MEMBER_ID to integer
    st.dataframe(df.style.highlight_max(axis=0))
else:
    st.write("No logs for today")