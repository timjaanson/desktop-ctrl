import { useEffect, useState } from "react";
import api from "../../Api/Api";
import URLS from "../../Api/Constants";
import { ReactComponent as BrokenHeart } from "../../icons/broken-heart.svg"

const MediaControl = () => {
    const [volume, setVolume] = useState(null)
    const [isMuted, setMuted] = useState(null)
    const [defaultValuesSet, setDefaultValuesSet] = useState(false)
    const [updateTime, setUpdateTime] = useState(new Date().getMilliseconds())
    const [automaticUpdateintervalId, setAutomaticUpdateintervalId] = useState(null)
    const [errorFetching, setErrorFetching] = useState(false)

    const getCurrentVolumeLevel = () => {
        api.get(URLS.VOLUME_CONTROL)
        .then(res => {
            setErrorFetching(false)
            setVolume(res.data.volumeLevel)
            setMuted(res.data.muted)
            setDefaultValuesSet(true)
        }).catch(err => {
            setErrorFetching(true)
            console.error(err)
        })
    }

    const enableInterval = () => {
        return setInterval(() => {
            getCurrentVolumeLevel()
        }, 2500)
    }

    useEffect(() => {
        setAutomaticUpdateintervalId(enableInterval())
        getCurrentVolumeLevel()
    },[])

    useEffect(() => {
        const timeout = setTimeout(() => {
            if (defaultValuesSet) {
                api.post(URLS.VOLUME_CONTROL, 
                    {
                        volumeLevel: volume,
                        muted: isMuted
                    }
                ).then(res => {
                    setVolume(res.data.volumeLevel)
                    setMuted(res.data.muted)
                }).finally(setAutomaticUpdateintervalId(enableInterval()))
            }
          }, 600)
          return () => {
            clearTimeout(timeout);
          }
    }, [updateTime])

    const handleVolumeSliderChange = (e) => {
        clearInterval(automaticUpdateintervalId)
        setVolume(e.target.value)
        setUpdateTime(new Date().getMilliseconds())
    }

    return (
        <div className="MediaControl">
            <h3>Media Controls</h3>
            <div>
                { errorFetching &&
                    <div style={{color: "red"}}>
                        <div style={{width: "2rem", height: "2rem", margin: "auto", fill: "red"}}>
                            <BrokenHeart />
                        </div>
                        <p>Error fetching data</p>
                    </div>
                }
                { volume != null && isMuted != null &&
                    <>
                        <div style={{
                            display: "grid",
                            gridTemplateColumns: "1fr 1fr",
                            gridGap: "1rem",
                            width: "15rem",
                            margin: "auto"
                        }}>
                            <p>volume:{volume}</p>
                            <p>muted: {JSON.stringify(isMuted)}</p>
                        </div>
                        <input
                            style={{
                                height: "10vh",
                                maxHeight: "50px",
                                width: "100%"
                            }}
                            type="range"
                            min="0"
                            max="100"
                            value={volume}
                            onChange={handleVolumeSliderChange} />
                    </>
                }
            </div>

        </div>
    )
}

export default MediaControl;