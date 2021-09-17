package Request.Delegate;

import Core.Model.ClientResponseModel;
import Core.Model.TrackException;

public interface IClientResponseReceive {
    public void ClientResponseReceive(ClientResponseModel request) throws TrackException;
}
