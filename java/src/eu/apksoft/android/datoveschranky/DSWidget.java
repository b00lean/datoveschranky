/*
 *  Datove schranky (http://github.com/b00lean/datoveschranky)
 *  Copyright (C) 2010  Karel Kyovsky <karel.kyovsky at apksoft.eu>
 *
 *  This file is part of Datove schranky (http://github.com/b00lean/datoveschranky).
 *
 *  Datove schranky is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License
 *  
 *  Datove schranky is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Datove schranky.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package eu.apksoft.android.datoveschranky;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import eu.apksoft.android.datoveschranky.services.DataBoxServicesImpl;
import eu.apksoft.android.datoveschranky.ws.DSUtils;


public class DSWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
	}

	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // To prevent any ANR timeouts, we perform the update in a service
        context.startService(new Intent(context, UpdateService.class));
    }
    
    public static class UpdateService extends Service {

    	@Override
        public void onStart(Intent intent, int startId) {
            // Build the widget update for today
            RemoteViews updateViews = buildUpdate(this);
            
            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, DSWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
            stopSelf();
        }

        public RemoteViews buildUpdate(Context context) {
        	int numberOfNewMessages = getNumberOfNewMessages(context);
        	// Build an update that holds the updated widget contents
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            if (numberOfNewMessages == 0) {
            	updateViews.setTextViewText(R.id.txtLabel, "");
            	updateViews.setImageViewResource(R.id.imgLabel, R.drawable.widget);
            }else{
            	updateViews.setTextViewText(R.id.txtLabel, "" + numberOfNewMessages);
            	updateViews.setImageViewResource(R.id.imgLabel, R.drawable.widget_new);
            }
            
            
            AndroidUtils.checkPasswordExpirations(this);
            
            // When user clicks on widget, launch Login page
            Intent defineIntent = new Intent(this, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, defineIntent, 0 );
            updateViews.setOnClickPendingIntent(R.id.txtLabel, pendingIntent);
           
            return updateViews;
        }
        
        private int getNumberOfNewMessages(Context context) {
        	AndroidUtils.initSSLIfNeeded(this);
        	
        	int numberOfUnknownMessages = 0;
			List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
			for (DataBoxAccess dataBoxAccess : dataBoxAccesses) {
				try {
					DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.SERVICE_URL, dataBoxAccess.getPersonId(), dataBoxAccess.getPassword());
					
					DataBoxMessagesService dataBoxMessagesService = services.getDataBoxMessagesService();

					
					GregorianCalendar from = new GregorianCalendar();
					GregorianCalendar to = new GregorianCalendar();
					from.add(Calendar.DAY_OF_YEAR, -200);
					to.add(Calendar.DAY_OF_YEAR, 1);
					EnumSet<MessageState> state = null;
					
					List<MessageEnvelope> messages = dataBoxMessagesService.getListOfReceivedMessages(from, to, state, 0, 100);
					
					String lastKnownMessageId = dataBoxAccess.getLastKnownMessageId();
					//count messages until you reach known messageId
					for (MessageEnvelope envelope : messages) {
						String messageID = envelope.getMessageID();
						if (!messageID.equalsIgnoreCase(lastKnownMessageId)){
							numberOfUnknownMessages++;
						}else{
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return numberOfUnknownMessages;
		}

		@Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
    }
}