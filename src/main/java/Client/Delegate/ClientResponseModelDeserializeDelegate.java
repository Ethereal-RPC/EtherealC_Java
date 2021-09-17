package Client.Delegate;

import Core.Model.ClientResponseModel;

public interface ClientResponseModelDeserializeDelegate {
    ClientResponseModel Deserialize(String obj);
}
