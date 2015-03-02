clear all;

%%%%%%%%%%%%%%
% Parameters %
%%%%%%%%%%%%%%
P.m = 1270;             %kg
P.b = 10;              %kg/s
P.Vmax = 27;            %m/s
% P.Vmax = 201*1000/3600;
P.Amax = 2.85;          %m/s^2
% P.Fmax = P.Amax*P.m + 50000000000;    %N max acceleration * mass
% t = P.Fmax/P.Vmax; %Gain
% P.Fmax = P.b*P.Vmax + P.Amax*P.m;
% P.Kp = P.Fmax/P.Vmax;
P.Kp = 600;
P.Kp_stop = 0.1*10^1;
% P.Fmin = -P.m*0.938 - 500000000;    %N max braking * mass
P.Ts   = 0.01;          %s

%%%%%%%%%%%%%%%
% State Space %
%%%%%%%%%%%%%%%
P.F    = [-P.b/P.m 0;1 0];
P.G    = [1/P.m; 0];
P.H    = [0 1];
P.Ji   = 0;
P.p    = [-.2+1j -.2-1j];
P.K    = place(P.F,P.G,P.p);
P.NN   = [P.F P.G; P.H P.Ji]\[0; 0; 1];
P.Nx   = P.NN(1:2);
P.Nu   = P.NN(3);
P.Nbar = P.Nu + P.K*P.Nx;
