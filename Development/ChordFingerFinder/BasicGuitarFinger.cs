using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    abstract class CBasicGuitarFinger : CGuitarFinger
    {
        public static int CompareString(EString fst, EString sec)
        {
            return fst - sec;
        }

        public override string ToString()
        {
            string sResult = String.Empty;

            foreach (EString currString in Strings)
        	{
		        sResult += currString.ToString() + " : " + Fret.ToString() + " ";
	        }
            sResult += "\n";
            return sResult;
        }

        public override bool Equals(object obj)
        {
            if(!obj.GetType().IsSubclassOf(typeof(CGuitarFinger)))
            {
                return false;
            }

            CGuitarFinger cFinger = (CGuitarFinger)obj;

            if (Fret != cFinger.Fret)
            {
                return false;
            }

            if (Strings.Count != cFinger.Strings.Count)
            {
                return false;
            }

            foreach (EString eCurrString in cFinger.Strings)
            {
                if (!Strings.Contains(eCurrString))
                {
                    return false;
                }
            }

            return true;
        }

        public override bool IsValid()
        {
            bool fResult = (Strings.Count > 0) && (Fret > 0) && (Fret <= Globals.NUMBER_OF_FRETS);

            if (fResult)
            {
                foreach (EString eCurrString in Strings)
                {
                    if (eCurrString > EString.LastString || eCurrString < EString.FirstString)
                    {
                        return false;
                    }
                }
            }

            return fResult;
        }

        public override bool CanPlay(CGuitarFinger cPos)
        {
            return cPos.IsValid();
        }

        public override int Priority
        {
            get
            {
                return m_nPriority;
            }

            protected set
            {
                if (m_nPriority < value)
                {
                    m_nPriority = value;
                }
            }
        }

        protected override bool GetPriorityForPlay(CFingerPosition cFngr, out int o_nPriority)
        {
            return GetPriorityForPlay(cFngr, false,out o_nPriority);
        }

        public override bool Add(CFingerPosition cPos)
        {
            bool fFuckIt;
            return Add(cPos, out fFuckIt);
        }

        public override bool Add(CFingerPosition cPos, out bool fCapo)
        {
            fCapo = false;

            if (Fret == -1)
            {
                int nPriority;
                if (!GetPriorityForPlay(cPos, out nPriority))
                {
                    return false;
                }
                Priority = nPriority;
                Fret = cPos.Fret;
                Strings.Add(cPos.String);
                return true;
            }

            Strings.Sort(new Comparison<EString>(CompareString));

            if (cPos.Fret == Fret)
            {
                bool fToAdd = (Strings.Count == 0);

                // Going through the strings.
                foreach (EString eCurr in Strings)
                {
                    if (Math.Abs(eCurr - cPos.String) == 1)
                    {
                        fCapo = true;
                        fToAdd = true;
                    }

                    if (eCurr == cPos.String)
                    {
                        return true;
                    }
                }

                if (fToAdd)
                {
                    Strings.Add(cPos.String);
                    int nPriority;
                    if (!GetPriorityForPlay(cPos, true,out nPriority))
                    {
                        return false;
                    }
                    Priority = nPriority;
                    return true;
                }
            }

            return false;
        }

        public CBasicGuitarFinger(CGuitarFinger other)
        {
            Fret = other.Fret;
            Strings = new List<EString>();
            m_nPriority = int.MinValue;

            foreach (EString currString in other.Strings)
            {
                if (!Add(new CFingerPosition(currString, Fret)))
                {
                    throw new Exception("What the hell is going on?");
                }
            }
        }

        public CBasicGuitarFinger(List<CGuitarFinger> lstOther)
        {
            m_nPriority = int.MinValue;
            if (lstOther == null || lstOther.Count == 0)
            {
                Fret = -1;
                Strings = new List<EString>();
            }
            else
            {
                Fret = lstOther[0].Fret;
                Strings = new List<EString>();

                foreach (EString currString in lstOther[0].Strings)
                {
                    if (!Add(new CFingerPosition(currString, Fret)))
                    {
                        throw new Exception("What the hell is going on?!");
                    }
                }
            }
        }

        public CBasicGuitarFinger()
        {
            Fret = -1;
            Strings = new List<EString>();
            m_nPriority = int.MinValue;
        }
    };
}
