/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.common.support;

import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFutureListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A default implementation of {@link org.apache.mina.common.IoFuture}.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 */
public class DefaultIoFuture implements IoFuture
{
    private final IoSession session;
    private final Object lock;
    private List listeners;
    private Object result;
    private boolean ready;


    /**
     * Creates a new instance.
     *
     * @param session an {@link IoSession} which is associated with this future
     */
    public DefaultIoFuture( IoSession session )
    {
        this.session = session;
        this.lock = this;
    }

    /**
     * Creates a new instance which uses the specified object as a lock.
     */
    public DefaultIoFuture( IoSession session, Object lock )
    {
        if( lock == null )
        {
            throw new NullPointerException( "lock" );
        }
        this.session = session;
        this.lock = lock;
    }

    public IoSession getSession()
    {
        return session;
    }

    public Object getLock()
    {
        return lock;
    }

    public void join()
    {
        synchronized( lock )
        {
            while( !ready )
            {
                try
                {
                    lock.wait();
                }
                catch( InterruptedException e )
                {
                }
            }
        }
    }

    public boolean join( long timeoutInMillis )
    {
        long startTime = ( timeoutInMillis <= 0 ) ? 0 : System
                .currentTimeMillis();
        long waitTime = timeoutInMillis;

        synchronized( lock )
        {
            if( ready )
            {
                return ready;
            }
            else if( waitTime <= 0 )
            {
                return ready;
            }

            for( ;; )
            {
                try
                {
                    lock.wait( waitTime );
                }
                catch( InterruptedException e )
                {
                }

                if( ready )
                    return true;
                else
                {
                    waitTime = timeoutInMillis - ( System.currentTimeMillis() - startTime );
                    if( waitTime <= 0 )
                    {
                        return ready;
                    }
                }
            }
        }
    }

    public boolean isReady()
    {
        synchronized( lock )
        {
            return ready;
        }
    }

    /**
     * Sets the result of the asynchronous operation, and mark it as finished.
     */
    protected void setValue( Object newValue )
    {
        synchronized( lock )
        {
            // Allow only once.
            if( ready )
            {
                return;
            }

            result = newValue;
            ready = true;
            lock.notifyAll();

            notifyListeners();
        }
    }

    /**
     * Returns the result of the asynchronous operation.
     */
    protected Object getValue()
    {
        synchronized( lock )
        {
            return result;
        }
    }

    public void addListener( IoFutureListener listener )
    {
        if( listener == null )
        {
            throw new NullPointerException( "listener" );
        }

        synchronized( lock )
        {
            if(listeners == null)
            {
                 listeners = new ArrayList();
            }
            listeners.add( listener );
            if( ready )
            {
                listener.operationComplete( this );
            }
        }
    }

    public void removeListener( IoFutureListener listener )
    {
        if( listener == null )
        {
            throw new NullPointerException( "listener" );
        }

        synchronized( lock )
        {
            listeners.remove( listener );
        }
    }

    private void notifyListeners()
    {
        synchronized( lock )
        {

            if(listeners != null)
            {

                for( Iterator i = listeners.iterator(); i.hasNext(); ) {
                    ( ( IoFutureListener ) i.next() ).operationComplete( this );
                }
            }
        }
    }
}



