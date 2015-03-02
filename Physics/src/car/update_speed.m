function X = update_speed(Vc, t, P)    

    persistent z
    persistent zdot
    persistent zddot
    persistent zddotd
    persistent zdotd
   
    if t == 0
        
%         Initialize
        z       = 0;
        zdot    = 0;
        zdotd   = 0;
        zddot   = 0;
        zddotd  = 0;
        control = 0;
        error   = Vc - zdot;
        
    else
        
%         Calculate Error and Force
%         error      = Vc - z;
        error      = Vc - zdot;
        control    = error * P.Kp;
        
%         Check Saturation
%         if control > P.Fmax
%             control = P.Fmax;
%         elseif control < P.Fmin;
%             control = P.Fmin;
%         end
        
%         Update States
        zddot = (control - P.b*zdot) / P.m;  
        zdot  = zdot + (P.Ts) * (zddot + zddotd) / 2;
        z     = z + (P.Ts) * (zdot + zdotd) / 2;
        
%         Update Delayed States
        zdotd  = zdot;
        zddotd = zddot;
        
    end
    
%     Assign States and Control as output
    X = [z, zdot, zddot, control, error];
    
end